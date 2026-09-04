<#
.SYNOPSIS
  Fails unless the Gradle plugin version equals the release tag/label.

.PARAMETER ReleaseLabel
  GitHub release tag or name (leading 'v' is optional).

.PARAMETER GradleVersion
  Version from build.gradle.kts.
#>
[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string] $ReleaseLabel,

    [Parameter(Mandatory = $true)]
    [string] $GradleVersion
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Normalize-ReleaseVersion {
    param([Parameter(Mandatory = $true)][string] $Value)
    $normalized = $Value.Trim()
    if ($normalized.StartsWith('v') -or $normalized.StartsWith('V')) {
        $normalized = $normalized.Substring(1)
    }
    return $normalized
}

$expected = Normalize-ReleaseVersion -Value $ReleaseLabel
$actual = $GradleVersion.Trim()

Write-Host "Release label: $ReleaseLabel (normalized: $expected)"
Write-Host "Gradle version: $actual"

if ($actual -cne $expected) {
    Write-Error "Release label version '$expected' must match build.gradle.kts version '$actual'."
    exit 1
}

Write-Host "Release version check passed."
exit 0
