@echo off
setlocal
cd /d "%~dp0.."
powershell -ExecutionPolicy Bypass -File ".\scripts\export-delivery-data.ps1"
endlocal
