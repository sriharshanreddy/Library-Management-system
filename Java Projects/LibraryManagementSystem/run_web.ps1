$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$out = Join-Path $root 'out'
$jarPath = Join-Path $root 'lib\mysql-connector-j-8.4.0.jar'

if (-not (Test-Path $out) -or -not (Test-Path $jarPath)) {
    & (Join-Path $root 'build.ps1')
}

Write-Host "Starting Library Management System Web Server..." -ForegroundColor Green
java -cp "$out;$jarPath" web.WebServer
