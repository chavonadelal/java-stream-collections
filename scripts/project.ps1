param(
    [ValidateSet('build', 'run')]
    [string]$Mode = 'run'
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot

function Invoke-JdkTool {
    param([string]$ToolName, [string[]]$ToolArguments)
    $executable = $ToolName
    if ($env:JAVA_HOME) {
        $executable = Join-Path $env:JAVA_HOME "bin/$ToolName.exe"
    }
    & $executable @ToolArguments
    if ($LASTEXITCODE -ne 0) {
        throw "$ToolName failed with exit code $LASTEXITCODE."
    }
}

Push-Location $projectRoot
try {
    New-Item -ItemType Directory -Path 'build/classes' -Force | Out-Null
    $mainSources = @(Get-ChildItem -LiteralPath 'src/main/java' -Filter '*.java' -Recurse |
        ForEach-Object { $_.FullName })
    Invoke-JdkTool 'javac' (@('--release', '17', '-encoding', 'UTF-8', '-Xlint:all', '-Werror',
        '-d', 'build/classes') + $mainSources)
    Invoke-JdkTool 'jar' @('--create', '--file', 'build/catalog.jar', '--main-class', 'catalog.Main',
        '-C', 'build/classes', '.')

    if ($Mode -eq 'run') {
        Invoke-JdkTool 'java' @('-Dfile.encoding=UTF-8', '-jar', 'build/catalog.jar')
    } else {
        Write-Output 'Built: build/catalog.jar'
    }
} finally {
    Pop-Location
}
