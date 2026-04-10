@echo off
setlocal enabledelayedexpansion
title Essentials Modpack Installer
echo.
echo  ===================================
echo   Essentials Modpack for MC 26.x
echo   One-Click Installer
echo  ===================================
echo.

set MC_MODS=%APPDATA%\.minecraft\mods
set JAVA_HOME=C:\Program Files\Java\jdk-25.0.2
set PACK_DIR=%~dp0

:: ============================================
:: CHECK FOR EXISTING MODS FIRST
:: ============================================
if exist "%MC_MODS%\*.jar" (
    set /a EXISTING=0
    for %%f in ("%MC_MODS%\*.jar") do set /a EXISTING+=1

    echo  [!!] WARNING: YOUR MODS FOLDER IS NOT EMPTY
    echo  =============================================
    echo.
    echo   Found !EXISTING! jar file^(s^) in:
    echo   %MC_MODS%
    echo.
    echo   The installer will DELETE ALL .jar files in
    echo   this folder and replace them with the modpack.
    echo.
    echo   If you have mods you want to keep, STOP NOW
    echo   and rename the folder first. For example:
    echo.
    echo     ren "%MC_MODS%" mods.old
    echo.
    echo  =============================================
    echo.
    set /p "WIPE1=  Do you understand ALL mods will be deleted? (y/n): "
    if /I not "!WIPE1!"=="y" (
        echo.
        echo  Installation cancelled.
        pause
        exit /b 0
    )
    echo.
    set /p "WIPE2=  Are you SURE? Type y again to confirm: "
    if /I not "!WIPE2!"=="y" (
        echo.
        echo  Installation cancelled.
        pause
        exit /b 0
    )
    echo.
    echo  Wiping mods folder...
    del /Q "%MC_MODS%\*.jar" 2>nul
    echo  [OK] Old mods removed.
    echo.
)

:: ============================================
:: ATTRIBUTION AND CONSENT
:: ============================================
echo  ---------------------------------------------------
echo.
echo  This installer will download mods created by other
echo  developers from Modrinth (modrinth.com). These mods
echo  are open-source projects maintained by their
echo  respective authors:
echo.
echo    Sodium, Lithium    - CaffeineMC
echo    Iris Shaders       - IrisShaders
echo    LambDynamicLights  - LambdAurora
echo    Chat Heads         - dzwdz
echo    JourneyMap         - TeamJM
echo    Mouse Tweaks       - YaLTeR
echo    Mod Menu           - TerraformersMC
echo    Cloth Config       - shedaniel
echo    Fabric API         - FabricMC
echo.
echo  By continuing, you agree to download these mods
echo  for use with the Essentials Modpack.
echo.
echo  ---------------------------------------------------
echo.
set /p "CONFIRM=  Type YES to continue: "
if /I not "!CONFIRM!"=="YES" (
    echo.
    echo  Installation cancelled.
    pause
    exit /b 0
)
echo.

:: ============================================
:: CHECK PREREQUISITES
:: ============================================
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo [ERROR] Java 25 not found at %JAVA_HOME%
    echo         Download from https://adoptium.net/ or https://jdk.java.net/25/
    pause
    exit /b 1
)

if not exist "%APPDATA%\.minecraft\versions\fabric-loader-*" (
    echo [WARN] Fabric Loader may not be installed.
    echo        Install from https://fabricmc.net/use/installer/
    echo        Select Minecraft 26.1.1, then click Install.
    echo.
)

:: ============================================
:: STEP 1: BUILD ESSENTIALS
:: ============================================
echo Step 1/3: Building Essentials mod...
echo.
pushd "%PACK_DIR%essentials"
call .\gradlew.bat build --no-daemon
if errorlevel 1 (
    echo.
    echo [ERROR] Build failed. Make sure Java 25 is installed.
    pause
    exit /b 1
)
popd
:: Verify at least one jar was produced
set "FOUND_JAR="
for %%f in ("%PACK_DIR%essentials\build\libs\essentials-*.jar") do set "FOUND_JAR=1"
if not defined FOUND_JAR (
    echo [ERROR] Essentials jar not found after build.
    pause
    exit /b 1
)
echo [OK] Essentials mod built.
echo.

:: ============================================
:: STEP 2: DOWNLOAD MODS
:: ============================================
echo Step 2/3: Downloading mods from Modrinth...
echo.
:: Clear local cache to prevent stale jars
if exist "%PACK_DIR%mods" rd /S /Q "%PACK_DIR%mods"
mkdir "%PACK_DIR%mods"

:: ============================================
:: AUTO-RESOLVE LATEST VERSIONS FROM MODRINTH
:: ============================================
:: Each mod is fetched by its Modrinth project ID using the API
:: to find the latest Fabric-compatible version. This means the
:: installer always grabs the newest build — no hardcoded URLs.

set MC_VER=26.1.2
set LOADER=fabric
set API=https://api.modrinth.com/v2

:: Helper: download latest version of a Modrinth mod
:: Usage: call :dl_mod "Display Name" "project_id"
call :dl_mod "Fabric API"         "P7dR8mSH"
call :dl_mod "Cloth Config"       "9s6osm5g"
call :dl_mod "Mod Menu"           "mOgUt4GM"
call :dl_mod "Sodium"             "AANobbMI"
call :dl_mod "Lithium"            "gvQqBUqZ"
call :dl_mod "Iris Shaders"       "YL57xq9U"
call :dl_mod "LambDynamicLights"  "yBW8D80W"
call :dl_mod "Chat Heads"         "Wb5oqrBJ"
call :dl_mod "JourneyMap"         "lfHFW1mp"
call :dl_mod "Mouse Tweaks"       "aC3cM3Vq"

:: Essentials (built from source)
echo   Essentials...
:: Find the built jar dynamically (matches any version)
set "ESSENTIALS_JAR="
for %%f in ("%PACK_DIR%essentials\build\libs\essentials-*.jar") do (
    echo %%~nxf | findstr /V "sources" >nul && set "ESSENTIALS_JAR=%%f"
)
if not defined ESSENTIALS_JAR (
    echo [ERROR] Essentials jar not found after build.
    pause
    exit /b 1
)
copy "%ESSENTIALS_JAR%" "%PACK_DIR%mods\" >nul
echo.
echo [OK] All mods downloaded.
echo.

:: ============================================
:: STEP 3: INSTALL TO .MINECRAFT
:: ============================================
echo Step 3/3: Installing to .minecraft...
echo.
if not exist "%MC_MODS%" mkdir "%MC_MODS%"

:: Copy fresh jars
for %%f in ("%PACK_DIR%mods\*.jar") do copy /Y "%%f" "%MC_MODS%\" >nul

set /a count=0
for %%f in ("%MC_MODS%\*.jar") do set /a count+=1
echo [OK] !count! mods installed to %MC_MODS%
echo.
echo  ===================================
echo   Installation complete!
echo.
echo   Launch Minecraft with your
echo   Fabric Loader profile.
echo.
echo   Config: Mod Menu ^> Essentials
echo   Keybinds: C=Zoom H=Fullbright
echo             `=Auto-click F11=Borderless
echo  ===================================
echo.
pause
exit /b 0

:: ============================================
:: SUBROUTINE: Download latest Fabric version
:: of a Modrinth mod by project ID.
:: Uses a temp PowerShell script for reliable
:: JSON parsing. Tries MC_VER, then 26.1.1, 26.1.
:: ============================================
:dl_mod
set "MOD_NAME=%~1"
set "MOD_ID=%~2"
echo   %MOD_NAME%...

:: Write PowerShell helper to temp file (avoids all escaping issues)
set "PS_SCRIPT=%TEMP%\essentials-dl.ps1"
(
echo $ErrorActionPreference = 'SilentlyContinue'
echo $api = 'https://api.modrinth.com/v2/project/%MOD_ID%/version'
echo foreach ^($v in @^('%MC_VER%','26.1.1','26.1'^)^) {
echo     $url = "$api`?loaders=[`"fabric`"]`&game_versions=[`"$v`"]"
echo     $r = Invoke-RestMethod -Uri $url -TimeoutSec 15
echo     if ^($r -and $r.Count -gt 0^) {
echo         $f = $r[0].files ^| Where-Object { $_.primary } ^| Select-Object -First 1
echo         if ^($f^) { Write-Output $f.url; exit 0 }
echo     }
echo }
) > "!PS_SCRIPT!"

set "DL_URL="
for /f "delims=" %%u in ('powershell -NoProfile -ExecutionPolicy Bypass -File "!PS_SCRIPT!"') do (
    set "DL_URL=%%u"
)
del "!PS_SCRIPT!" 2>nul

if not defined DL_URL (
    echo     [WARN] No compatible version found for %MOD_NAME%
    goto :eof
)

:: Extract filename from URL and decode %%2B to +
for %%f in ("!DL_URL!") do set "DL_FILE=%%~nxf"
set "DL_FILE=!DL_FILE:%%2B=+!"

curl -sL "!DL_URL!" -o "%PACK_DIR%mods\!DL_FILE!"
if exist "%PACK_DIR%mods\!DL_FILE!" (
    echo     [OK] !DL_FILE!
) else (
    echo     [WARN] Failed to download %MOD_NAME%
)
goto :eof
