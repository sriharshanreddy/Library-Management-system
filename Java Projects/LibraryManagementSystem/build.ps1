$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$src = Join-Path $root 'src'
$out = Join-Path $root 'out'
$lib = Join-Path $root 'lib'
$jarName = 'mysql-connector-j-8.4.0.jar'
$jarPath = Join-Path $lib $jarName
$downloadUrl = 'https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.4.0/mysql-connector-j-8.4.0.jar'

if (-not (Test-Path $lib)) {
    New-Item -ItemType Directory -Path $lib | Out-Null
}

if (-not (Test-Path $jarPath)) {
    Write-Host 'Downloading MySQL connector...'
    Invoke-WebRequest -Uri $downloadUrl -OutFile $jarPath
}

if (Test-Path $out) {
    Remove-Item -Recurse -Force $out
}

New-Item -ItemType Directory -Path $out | Out-Null

$sources = Get-ChildItem -Path $src -Filter *.java -Recurse | ForEach-Object { $_.FullName }
if (-not $sources) {
    throw 'No Java source files were found under src.'
}

javac -cp $jarPath -d $out @sources

Write-Host 'Build completed successfully.'
Write-Host "Run with: java -cp `"$out;$jarPath`" ui.Main"