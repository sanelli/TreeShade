<#
.SYNOPSIS
  Reads the plugin version from a Gradle Kotlin build script.

.PARAMETER Path
  Path to build.gradle.kts (or any file containing version = "...").

.PARAMETER Content
  Raw file content (string or line array from git show). Use instead of Path.
#>
[CmdletBinding()]
param(
    [Parameter(ParameterSetName = 'Path')]
    [string] $Path,

    [Parameter(ParameterSetName = 'Content')]
    [AllowNull()]
    [object] $Content
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function ConvertTo-SingleString {
    param([AllowNull()][object] $Value)

    if ($null -eq $Value) {
        return ''
    }

    if ($Value -is [string]) {
        return $Value
    }

    if ($Value -is [System.Collections.IEnumerable]) {
        return (($Value | ForEach-Object { "$_" }) -join "`n")
    }

    return [string]$Value
}

if ($PSCmdlet.ParameterSetName -eq 'Path') {
    if (-not (Test-Path -LiteralPath $Path)) {
        throw "File not found: $Path"
    }
    $scriptText = Get-Content -LiteralPath $Path -Raw
}
else {
    $scriptText = ConvertTo-SingleString -Value $Content
}

if ([string]::IsNullOrWhiteSpace($scriptText)) {
    throw 'No content provided to parse for version.'
}

# Prefer the project version assignment (not plugin version = "..." lines).
$match = [regex]::Match(
    $scriptText,
    '(?m)^\s*version\s*=\s*"(?<version>[^"]+)"\s*$'
)

if (-not $match.Success) {
    throw 'Could not find version = "..." in the provided Gradle script.'
}

$match.Groups['version'].Value
