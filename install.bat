@echo off
REM Installs the Essentials modpack to your .minecraft/mods folder
REM Run download-mods.bat first!

set MC_MODS=%APPDATA%\.minecraft\mods

echo === Essentials Modpack Installer ===
echo.
echo Target: %MC_MODS%
echo.

if not exist mods\fabric-api-*.jar (
    echo ERROR: No mods downloaded yet. Run download-mods.bat first!
    pause
    exit /b 1
)

if not exist "%MC_MODS%" mkdir "%MC_MODS%"

echo Copying mods...
copy /Y mods\*.jar "%MC_MODS%\" >nul
echo.

dir /b "%MC_MODS%\*.jar" | find /c ".jar"
echo mod(s) installed to %MC_MODS%

echo.
echo Make sure Fabric Loader 0.18.6+ is installed for MC 26.1
echo Launch Minecraft with the fabric-loader-26.1 profile
echo.
pause
