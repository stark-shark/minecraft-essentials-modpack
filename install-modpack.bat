@echo off
title Essentials Modpack Installer
echo.
echo  ===================================
echo   Essentials Modpack for MC 26.1
echo   One-Click Installer
echo  ===================================
echo.

set MC_MODS=%APPDATA%\.minecraft\mods
set JAVA_HOME=C:\Program Files\Java\jdk-25.0.2

:: Check Java
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo [ERROR] Java 25 not found at %JAVA_HOME%
    echo         Download from https://adoptium.net/ or https://jdk.java.net/25/
    pause
    exit /b 1
)

:: Check Fabric
if not exist "%APPDATA%\.minecraft\versions\fabric-loader-*" (
    echo [WARN] Fabric Loader may not be installed.
    echo        Install from https://fabricmc.net/use/installer/
    echo        Select Minecraft 26.1, then click Install.
    echo.
)

echo Step 1/3: Building Essentials mod...
echo.
cd essentials
call .\gradlew.bat build --no-daemon -q 2>nul
if errorlevel 1 (
    echo [ERROR] Build failed. Make sure Java 25 is installed.
    pause
    exit /b 1
)
cd ..
echo [OK] Essentials mod built.
echo.

echo Step 2/3: Downloading mods from Modrinth...
echo.
if not exist mods mkdir mods

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
curl -sL "https://cdn.modrinth.com/data/gvQqBUqZ/versions/W0ZXKJy9/lithium-fabric-0.22.1%%2Bmc26.1.jar" -o "mods\lithium-fabric-0.22.1+mc26.1.jar"
echo   Entity Culling...
curl -sL "https://cdn.modrinth.com/data/NNAgCjsB/versions/YSbzFHRt/entityculling-fabric-1.10.0-mc26.1.jar" -o "mods\entityculling-fabric-1.10.0-mc26.1.jar"

:: Visual
echo   Iris Shaders...
curl -sL "https://cdn.modrinth.com/data/YL57xq9U/versions/Yi5E3d2l/iris-fabric-1.10.8%%2Bmc26.1.jar" -o "mods\iris-fabric-1.10.8+mc26.1.jar"
echo   LambDynamicLights...
curl -sL "https://cdn.modrinth.com/data/yBW8D80W/versions/Nttq3ROe/lambdynamiclights-4.10.0%%2B26.1.jar" -o "mods\lambdynamiclights-4.10.0+26.1.jar"
echo   Status Effect Bars...
curl -sL "https://cdn.modrinth.com/data/x02cBj9Y/versions/HPrO2zBi/status-effect-bars-1.0.11.jar" -o "mods\status-effect-bars-1.0.11.jar"
echo   Chat Heads...
curl -sL "https://cdn.modrinth.com/data/Wb5oqrBJ/versions/J5xd8lnJ/chat_heads-1.2.2-fabric-26.1.jar" -o "mods\chat_heads-1.2.2-fabric-26.1.jar"

:: Map
echo   JourneyMap...
curl -sL "https://cdn.modrinth.com/data/lfHFW1mp/versions/1lcmIgq5/journeymap-fabric-26.1-6.0.0-beta.64.jar" -o "mods\journeymap-fabric-26.1-6.0.0-beta.64.jar"

:: QoL
echo   Mouse Tweaks...
curl -sL "https://cdn.modrinth.com/data/aC3cM3Vq/versions/EBIKCzuP/MouseTweaks-fabric-mc26.1-2.31.jar" -o "mods\MouseTweaks-fabric-mc26.1-2.31.jar"
echo   Controlling...
curl -sL "https://cdn.modrinth.com/data/xv94TkTM/versions/2rMoGipz/Controlling-fabric-26.1-26.1.0.1.jar" -o "mods\Controlling-fabric-26.1-26.1.0.1.jar"
echo   No Chat Reports...
curl -sL "https://cdn.modrinth.com/data/qQyHxfxd/versions/2yrLNE3S/NoChatReports-FABRIC-26.1-v2.19.0.jar" -o "mods\NoChatReports-FABRIC-26.1-v2.19.0.jar"

:: Essentials
copy essentials\build\libs\essentials-0.1.0+26.1.jar mods\ >nul
echo.
echo [OK] All mods downloaded.
echo.

echo Step 3/3: Installing to .minecraft...
echo.
if not exist "%MC_MODS%" mkdir "%MC_MODS%"
copy /Y mods\*.jar "%MC_MODS%\" >nul

set /a count=0
for %%f in ("%MC_MODS%\*.jar") do set /a count+=1
echo [OK] %count% mods installed to %MC_MODS%
echo.
echo  ===================================
echo   Installation complete!
echo.
echo   Launch Minecraft with the
echo   fabric-loader-26.1 profile.
echo.
echo   Config: Mod Menu ^> Essentials
echo   Keybinds: C=Zoom H=Fullbright
echo             `=Auto-click F11=Borderless
echo  ===================================
echo.
pause
