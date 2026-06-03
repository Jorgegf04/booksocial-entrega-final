param(
  [string]$SqlInput = "data/mysql-init/01-booksocial.sql"
)

$ErrorActionPreference = "Stop"

if (!(Test-Path $SqlInput)) {
  throw "No existe $SqlInput. Ejecuta primero scripts/export-delivery-data.ps1 en el ordenador que tiene los datos."
}

Write-Host "Restaurando $SqlInput en MySQL ..."
Get-Content $SqlInput -Raw | docker compose exec -T mysql-db sh -c 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD"'

Write-Host "Restauracion terminada."
