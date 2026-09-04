<#
.SYNOPSIS
  Fails unless the candidate plugin version is strictly greater than the baseline.

.PARAMETER Candidate
  Version from the pull request branch.

.PARAMETER Baseline
  Version from the target branch (main).
#>
[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string] $Candidate,

    [Parameter(Mandatory = $true)]
    [string] $Baseline
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function ConvertFrom-PluginVersion {
    param([Parameter(Mandatory = $true)][string] $Version)

    $trimmed = $Version.Trim()
    $match = [regex]::Match(
        $trimmed,
        '^(?<major>\d+)\.(?<minor>\d+)\.(?<patch>\d+)(?:-(?<label>[A-Za-z]+)(?:\.(?<pre>\d+))?)?$'
    )

    if (-not $match.Success) {
        throw "Unsupported version format: '$Version'. Expected like 1.2.3 or 1.2.3-preview.4"
    }

    $hasPreRelease = $match.Groups['label'].Success
    [pscustomobject]@{
        Original         = $trimmed
        Major            = [int]$match.Groups['major'].Value
        Minor            = [int]$match.Groups['minor'].Value
        Patch            = [int]$match.Groups['patch'].Value
        PreReleaseLabel  = if ($hasPreRelease) { $match.Groups['label'].Value.ToLowerInvariant() } else { $null }
        PreReleaseNumber = if ($match.Groups['pre'].Success) { [int]$match.Groups['pre'].Value } else { 0 }
        IsPreRelease     = $hasPreRelease
    }
}

function Test-VersionGreaterThan {
    param(
        [Parameter(Mandatory = $true)] $Candidate,
        [Parameter(Mandatory = $true)] $Baseline
    )

    if ($Candidate.Major -ne $Baseline.Major) {
        return $Candidate.Major -gt $Baseline.Major
    }
    if ($Candidate.Minor -ne $Baseline.Minor) {
        return $Candidate.Minor -gt $Baseline.Minor
    }
    if ($Candidate.Patch -ne $Baseline.Patch) {
        return $Candidate.Patch -gt $Baseline.Patch
    }

    # Same major.minor.patch: a release is greater than any pre-release;
    # equal releases are not greater; compare pre-release label/number otherwise.
    if (-not $Candidate.IsPreRelease -and -not $Baseline.IsPreRelease) {
        return $false
    }
    if (-not $Candidate.IsPreRelease -and $Baseline.IsPreRelease) {
        return $true
    }
    if ($Candidate.IsPreRelease -and -not $Baseline.IsPreRelease) {
        return $false
    }

    $labelCompare = [string]::Compare(
        $Candidate.PreReleaseLabel,
        $Baseline.PreReleaseLabel,
        [System.StringComparison]::Ordinal
    )
    if ($labelCompare -ne 0) {
        return $labelCompare -gt 0
    }

    return $Candidate.PreReleaseNumber -gt $Baseline.PreReleaseNumber
}

$candidateVersion = ConvertFrom-PluginVersion -Version $Candidate
$baselineVersion = ConvertFrom-PluginVersion -Version $Baseline

Write-Host "PR version:   $($candidateVersion.Original)"
Write-Host "Main version: $($baselineVersion.Original)"

if (-not (Test-VersionGreaterThan -Candidate $candidateVersion -Baseline $baselineVersion)) {
    Write-Error "Plugin version '$Candidate' must be strictly greater than main's '$Baseline'."
    exit 1
}

Write-Host "Version check passed: $Candidate > $Baseline"
exit 0
