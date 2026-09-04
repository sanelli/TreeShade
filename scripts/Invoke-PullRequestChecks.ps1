<#
.SYNOPSIS
  CI entrypoint for pull requests into main: version bump check + Gradle build.
#>
[CmdletBinding()]
param(
    [string] $RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path,
    [string] $BaselineRef = 'origin/main'
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$getVersion = Join-Path $PSScriptRoot 'Get-PluginVersion.ps1'
$assertGreater = Join-Path $PSScriptRoot 'Assert-PluginVersionGreater.ps1'
$gradleFile = Join-Path $RepoRoot 'build.gradle.kts'

Write-Host "==> Resolving plugin versions"
$candidate = & $getVersion -Path $gradleFile
$baselineLines = @(git -C $RepoRoot show "${BaselineRef}:build.gradle.kts" 2>&1)
if ($LASTEXITCODE -ne 0) {
    throw "Failed to read build.gradle.kts from $BaselineRef`: $($baselineLines -join "`n")"
}
$baselineContent = $baselineLines -join "`n"
if ([string]::IsNullOrWhiteSpace($baselineContent)) {
    throw "Failed to read build.gradle.kts from $BaselineRef (empty content)"
}
$baseline = & $getVersion -Content $baselineContent

& $assertGreater -Candidate $candidate -Baseline $baseline
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

Write-Host "==> Building plugin with Gradle (buildPlugin)"
Write-Host "    Using verbose Gradle logging (--info). First runs can take several minutes while Rider SDK artifacts download."
Push-Location $RepoRoot
try {
    $gradleArgs = @('buildPlugin') + @(& (Join-Path $PSScriptRoot 'Get-GradleCiArgs.ps1'))
    Write-Host ("    gradle " + ($gradleArgs -join ' '))
    & gradle @gradleArgs
    if ($LASTEXITCODE -ne 0) {
        throw "gradle buildPlugin failed with exit code $LASTEXITCODE"
    }
}
finally {
    Pop-Location
}

Write-Host "==> PR checks completed successfully"
