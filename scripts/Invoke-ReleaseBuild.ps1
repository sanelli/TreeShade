<#
.SYNOPSIS
  CI entrypoint for GitHub Releases: version match, signed Gradle build, zip path output.

.PARAMETER ReleaseLabel
  Release tag or name that must match build.gradle.kts version.

.PARAMETER RepoRoot
  Repository root.

.PARAMETER SignedZipPathOutput
  Optional path to write the absolute path of the signed distribution zip (for GITHUB_OUTPUT).
#>
[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string] $ReleaseLabel,

    [string] $RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path,

    [string] $SignedZipPathOutput
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$getVersion = Join-Path $PSScriptRoot 'Get-PluginVersion.ps1'
$assertMatch = Join-Path $PSScriptRoot 'Assert-ReleaseVersionMatches.ps1'
$gradleFile = Join-Path $RepoRoot 'build.gradle.kts'

Write-Host "==> Checking release version against Gradle"
$gradleVersion = & $getVersion -Path $gradleFile
& $assertMatch -ReleaseLabel $ReleaseLabel -GradleVersion $gradleVersion
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

foreach ($name in @('CERTIFICATE_CHAIN', 'PRIVATE_KEY', 'PRIVATE_KEY_PASSWORD')) {
    $value = [Environment]::GetEnvironmentVariable($name)
    if ([string]::IsNullOrWhiteSpace($value)) {
        throw "Missing required signing environment variable: $name"
    }
}

Write-Host "==> Building and signing plugin with Gradle (signPlugin)"
Push-Location $RepoRoot
try {
    gradle signPlugin --console=plain --no-daemon
    if ($LASTEXITCODE -ne 0) {
        throw "gradle signPlugin failed with exit code $LASTEXITCODE"
    }
}
finally {
    Pop-Location
}

$distributions = Join-Path $RepoRoot 'build/distributions'
$zip = Get-ChildItem -LiteralPath $distributions -Filter "*-$gradleVersion.zip" -File |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1

if ($null -eq $zip) {
    throw "Signed plugin zip not found in $distributions for version $gradleVersion"
}

Write-Host "==> Signed plugin zip: $($zip.FullName)"

if (-not [string]::IsNullOrWhiteSpace($SignedZipPathOutput)) {
    [System.IO.File]::WriteAllText($SignedZipPathOutput, $zip.FullName)
}

if ($env:GITHUB_OUTPUT) {
    Add-Content -LiteralPath $env:GITHUB_OUTPUT -Value "signed_zip=$($zip.FullName)"
}

Write-Host "==> Release build completed successfully"
