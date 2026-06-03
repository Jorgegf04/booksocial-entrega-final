param(
  [string]$SqlOutput = "data/mysql-init/01-booksocial.sql",
  [string]$UploadsOutput = "uploads"
)

$ErrorActionPreference = "Stop"

New-Item -ItemType Directory -Force -Path (Split-Path $SqlOutput) | Out-Null
New-Item -ItemType Directory -Force -Path $UploadsOutput | Out-Null

Write-Host "Exportando base de datos MySQL a $SqlOutput ..."
$dump = & docker compose exec -T mysql-db sh -c 'mysqldump -uroot -p"$MYSQL_ROOT_PASSWORD" --databases "$MYSQL_DATABASE" --add-drop-database --routines --events --triggers --default-character-set=utf8mb4'
if ($LASTEXITCODE -ne 0) {
  throw "mysqldump fallo con codigo $LASTEXITCODE"
}

$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText((Resolve-Path (Split-Path $SqlOutput)).Path + [System.IO.Path]::DirectorySeparatorChar + (Split-Path $SqlOutput -Leaf), ($dump -join "`n"), $utf8NoBom)

$bytes = [System.IO.File]::ReadAllBytes((Resolve-Path $SqlOutput).Path)
if ($bytes -contains 0) {
  throw "El SQL exportado contiene bytes nulos. Borra $SqlOutput y vuelve a ejecutar este script."
}

Write-Host "Copiando imagenes subidas desde el contenedor backend-api a $UploadsOutput ..."
docker cp backend-api:/app/images/. $UploadsOutput

Write-Host "Exportacion terminada."
Write-Host "Sube a GitHub el SQL generado y los archivos de uploads si quieres que otro ordenador tenga los mismos datos."
