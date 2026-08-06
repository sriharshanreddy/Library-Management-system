$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$out = Join-Path $root 'out'
$jarPath = Join-Path $root 'lib\mysql-connector-j-8.4.0.jar'

if (-not (Test-Path $out)) {
    & (Join-Path $root 'build.ps1')
}

if (-not (Test-Path $jarPath)) {
    & (Join-Path $root 'build.ps1')
}

java -cp "$out;$jarPath" ui.Main