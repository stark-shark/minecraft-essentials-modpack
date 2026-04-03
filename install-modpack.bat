@echo off
setlocal enabledelayedexpansion
title Essentials Modpack Installer
echo.
echo  ===================================
echo   Essentials Modpack for MC 26.1.1
echo   One-Click Installer
echo  ===================================
echo.

set MC_MODS=%APPDATA%\.minecraft\mods
set JAVA_HOME=C:\Program Files\Java\jdk-25.0.2

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
cd essentials
call .\gradlew.bat build --no-daemon
if errorlevel 1 (
    echo.
    echo [ERROR] Build failed. Make sure Java 25 is installed.
    pause
    exit /b 1
)
cd ..
if not exist "essentials\build\libs\essentials-0.1.0+26.1.jar" (
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
if exist mods rd /S /Q mods
mkdir mods

:: Dependencies
echo   Fabric API...
curl -sL "https://cdn.modrinth.com/data/P7dR8mSH/versions/G0yfY6x2/fabric-api-0.145.3%%2B26.1.1.jar" -o "mods\fabric-api-0.145.3+26.1.1.jar"
echo   Cloth Config...
curl -sL "https://cdn.modrinth.com/data/9s6osm5g/versions/GFM8zh9J/cloth-config-26.1.154.jar" -o "mods\cloth-config-26.1.154.jar"
echo   Mod Menu...
curl -sL "https://cdn.modrinth.com/data/mOgUt4GM/versions/jvjwXH6l/modmenu-18.0.0-alpha.8.jar" -o "mods\modmenu-18.0.0-alpha.8.jar"

:: Performance
echo   Sodium...
curl -sL "https://cdn.modrinth.com/data/AANobbMI/versions/Amr4VcZo/sodium-fabric-0.8.7%%2Bmc26.1.jar" -o "mods\sodium-fabric-0.8.7+mc26.1.jar"
echo   Lithium...
curl -sL "https://cdn.modrinth.com/data/gvQqBUqZ/versions/kHXOBNih/lithium-fabric-0.23.0%%2Bmc26.1.1.jar" -o "mods\lithium-fabric-0.23.0+mc26.1.1.jar"

:: Visual
echo   Iris Shaders...
curl -sL "https://cdn.modrinth.com/data/YL57xq9U/versions/Yi5E3d2l/iris-fabric-1.10.8%%2Bmc26.1.jar" -o "mods\iris-fabric-1.10.8+mc26.1.jar"
echo   LambDynamicLights...
curl -sL "https://cdn.modrinth.com/data/yBW8D80W/versions/Nttq3ROe/lambdynamiclights-4.10.0%%2B26.1.jar" -o "mods\lambdynamiclights-4.10.0+26.1.jar"
echo   Chat Heads...
curl -sL "https://cdn.modrinth.com/data/Wb5oqrBJ/versions/J5xd8lnJ/chat_heads-1.2.2-fabric-26.1.jar" -o "mods\chat_heads-1.2.2-fabric-26.1.jar"

:: Map
echo   JourneyMap...
curl -sL "https://cdn.modrinth.com/data/lfHFW1mp/versions/1lcmIgq5/journeymap-fabric-26.1-6.0.0-beta.64.jar" -o "mods\journeymap-fabric-26.1-6.0.0-beta.64.jar"

:: QoL
echo   Mouse Tweaks...
curl -sL "https://cdn.modrinth.com/data/aC3cM3Vq/versions/EBIKCzuP/MouseTweaks-fabric-mc26.1-2.31.jar" -o "mods\MouseTweaks-fabric-mc26.1-2.31.jar"

:: Essentials
echo   Essentials...
copy "essentials\build\libs\essentials-0.1.0+26.1.jar" "mods\essentials-0.1.0+26.1.jar" >nul
if not exist "mods\essentials-0.1.0+26.1.jar" (
    echo [ERROR] Failed to copy Essentials jar.
    pause
    exit /b 1
)
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
for %%f in (mods\*.jar) do copy /Y "%%f" "%MC_MODS%\" >nul

set /a count=0
for %%f in ("%MC_MODS%\*.jar") do set /a count+=1
echo [OK] !count! mods installed to %MC_MODS%
echo.
echo  ===================================
echo   Installation complete!
echo.
echo   Launch Minecraft with the
echo   fabric-loader-26.1.1 profile.
echo.
echo   Config: Mod Menu ^> Essentials
echo   Keybinds: C=Zoom H=Fullbright
echo             `=Auto-click F11=Borderless
echo  ===================================
echo.
pause
