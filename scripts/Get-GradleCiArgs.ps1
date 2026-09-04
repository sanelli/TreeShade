<#
.SYNOPSIS
  Shared Gradle CLI flags for CI builds (no daemon, plain + info logging).
#>
[CmdletBinding()]
param()

@(
    '--console=plain'
    '--no-daemon'
    '--info'
    '--stacktrace'
)
