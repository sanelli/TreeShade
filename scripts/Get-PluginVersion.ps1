<#
.SYNOPSIS
  Reads the plugin version from a Gradle Kotlin build script.

.PARAMETER Path
  Path to build.gradle.kts (or any file containing version = "...").

.PARAMETER Content
  Raw file content. Use instead of Path when reading from git show.
#>
[CmdletBinding()]
param(
    [Parameter(ParameterSetName = 'Path')]
    [string] $Path,

    [Parameter(ParameterSetName = 'Content')]
    [string] $Content
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

if ($PSCmdlet.ParameterSetName -eq 'Path') {
    if (-not (Test-Path -LiteralPath $Path)) {
        throw "File not found: $Path"
    }
    $Content = Get-Content -LiteralPath $Path -Raw
}

if ([string]::IsNullOrWhiteSpace($Content)) {
    throw 'No content provided to parse for version.'
}

# Prefer the project version assignment (not plugin version = "..." lines).
$match = [regex]::Match(
    $Content,
    '(?m)^\s*version\s*=\s*"(?<version>[^"]+)"\s*$'
)

if (-not $match.Success) {
    throw 'Could not find version = "..." in the provided Gradle script.'
}

$match.Groups['version'].Value
