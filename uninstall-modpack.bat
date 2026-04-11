@echo off
title Essentials Modpack Uninstaller
echo.
echo  ===================================
echo   Essentials Modpack Uninstaller
echo  ===================================
echo.

set MC_MODS=%APPDATA%\.minecraft\mods
set MC_CONFIG=%APPDATA%\.minecraft\config

echo  This will remove the following from your .minecraft folder:
echo.
echo  Mods (from %MC_MODS%):
echo    - essentials-*.jar
echo    - fabric-api-*.jar
echo    - cloth-config-*.jar
echo    - modmenu-*.jar
echo    - sodium-fabric-*.jar
echo    - lithium-fabric-*.jar
echo    - iris-fabric-*.jar
echo    - lambdynamiclights-*.jar
echo    - chat_heads-*.jar
echo    - journeymap-fabric-*.jar
echo    - MouseTweaks-fabric-*.jar
echo.
echo  Config:
echo    - %MC_CONFIG%\essentials.properties
echo    - %MC_CONFIG%\essentials\ (ender chest cache)
echo.
echo  Nothing else will be touched.
echo.
set /p CONFIRM=  Type YES to uninstall:
if /I not "%CONFIRM%"=="YES" (
    echo.
    echo  Uninstall cancelled.
    pause
    exit /b 0
)

echo.
echo  Removing mods...

del /Q "%MC_MODS%\essentials-*.jar" 2>nul
del /Q "%MC_MODS%\fabric-api-*.jar" 2>nul
del /Q "%MC_MODS%\cloth-config-*.jar" 2>nul
del /Q "%MC_MODS%\modmenu-*.jar" 2>nul
del /Q "%MC_MODS%\sodium-fabric-*.jar" 2>nul
del /Q "%MC_MODS%\lithium-fabric-*.jar" 2>nul
del /Q "%MC_MODS%\iris-fabric-*.jar" 2>nul
del /Q "%MC_MODS%\lambdynamiclights-*.jar" 2>nul
del /Q "%MC_MODS%\chat_heads-*.jar" 2>nul
del /Q "%MC_MODS%\journeymap-fabric-*.jar" 2>nul
del /Q "%MC_MODS%\MouseTweaks-fabric-*.jar" 2>nul

echo  Removing config...
del /Q "%MC_CONFIG%\essentials.properties" 2>nul
if exist "%MC_CONFIG%\essentials" rd /S /Q "%MC_CONFIG%\essentials" 2>nul

echo.
echo  ===================================
echo   Uninstall complete!
echo.
echo   Your .minecraft folder is clean.
echo   Fabric Loader is still installed
echo   (remove via Minecraft Launcher
echo   if you no longer need it).
echo  ===================================
echo.
pause
