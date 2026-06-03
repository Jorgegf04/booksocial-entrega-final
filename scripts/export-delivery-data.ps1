param(
  [string]$SqlOutput = "data/mysql-init/01-booksocial.sql",
  [string]$UploadsOutput = "uploads"
)

$ErrorActionPreference = "Stop"

New-Item -ItemType Directory -Force -Path (Split-Path $SqlOutput) | Out-Null
New-Item -ItemType Directory -Force -Path $UploadsOutput | Out-Null

Write-Host "Exportando base de datos MySQL a $SqlOutput ..."
docker compose exec -T mysql-db sh -c 'mysqldump -uroot -p"$MYSQL_ROOT_PASSWORD" --databases "$MYSQL_DATABASE" --add-drop-database --routines --events --triggers' > $SqlOutput

Write-Host "Copiando imagenes subidas desde el contenedor backend-api a $UploadsOutput ..."
docker cp backend-api:/app/images/. $UploadsOutput

Write-Host "Exportacion terminada."
Write-Host "Sube a GitHub el SQL generado y los archivos de uploads si quieres que otro ordenador tenga los mismos datos."
