@echo off
REM Downloads all modpack mods from Modrinth for MC 26.1 Fabric
REM Run from the modpack root directory

if not exist mods mkdir mods

echo === Essentials Modpack — Downloading mods for MC 26.1 ===
echo.

echo --- Dependencies ---
echo [GET] Fabric API
curl -sL "https://cdn.modrinth.com/data/P7dR8mSH/versions/G0yfY6x2/fabric-api-0.145.3%%2B26.1.1.jar" -o "mods\fabric-api-0.145.3+26.1.1.jar"
echo [GET] Cloth Config
curl -sL "https://cdn.modrinth.com/data/9s6osm5g/versions/GFM8zh9J/cloth-config-26.1.154.jar" -o "mods\cloth-config-26.1.154.jar"
echo [GET] Mod Menu
curl -sL "https://cdn.modrinth.com/data/mOgUt4GM/versions/jvjwXH6l/modmenu-18.0.0-alpha.8.jar" -o "mods\modmenu-18.0.0-alpha.8.jar"

echo.
echo --- Performance ---
echo [GET] Sodium
curl -sL "https://cdn.modrinth.com/data/AANobbMI/versions/Amr4VcZo/sodium-fabric-0.8.7%%2Bmc26.1.jar" -o "mods\sodium-fabric-0.8.7+mc26.1.jar"
echo [GET] Lithium
curl -sL "https://cdn.modrinth.com/data/gvQqBUqZ/versions/W0ZXKJy9/lithium-fabric-0.22.1%%2Bmc26.1.jar" -o "mods\lithium-fabric-0.22.1+mc26.1.jar"
echo [GET] Entity Culling
curl -sL "https://cdn.modrinth.com/data/NNAgCjsB/versions/YSbzFHRt/entityculling-fabric-1.10.0-mc26.1.jar" -o "mods\entityculling-fabric-1.10.0-mc26.1.jar"

echo.
echo --- Visual ---
echo [GET] Iris Shaders
curl -sL "https://cdn.modrinth.com/data/YL57xq9U/versions/Yi5E3d2l/iris-fabric-1.10.8%%2Bmc26.1.jar" -o "mods\iris-fabric-1.10.8+mc26.1.jar"
echo [GET] LambDynamicLights
curl -sL "https://cdn.modrinth.com/data/yBW8D80W/versions/Nttq3ROe/lambdynamiclights-4.10.0%%2B26.1.jar" -o "mods\lambdynamiclights-4.10.0+26.1.jar"
echo [GET] Status Effect Bars
curl -sL "https://cdn.modrinth.com/data/x02cBj9Y/versions/HPrO2zBi/status-effect-bars-1.0.11.jar" -o "mods\status-effect-bars-1.0.11.jar"
echo [GET] Chat Heads
curl -sL "https://cdn.modrinth.com/data/Wb5oqrBJ/versions/J5xd8lnJ/chat_heads-1.2.2-fabric-26.1.jar" -o "mods\chat_heads-1.2.2-fabric-26.1.jar"

echo.
echo --- Quality of Life ---
echo [GET] Mouse Tweaks
curl -sL "https://cdn.modrinth.com/data/aC3cM3Vq/versions/EBIKCzuP/MouseTweaks-fabric-mc26.1-2.31.jar" -o "mods\MouseTweaks-fabric-mc26.1-2.31.jar"
echo [GET] Controlling
curl -sL "https://cdn.modrinth.com/data/xv94TkTM/versions/2rMoGipz/Controlling-fabric-26.1-26.1.0.1.jar" -o "mods\Controlling-fabric-26.1-26.1.0.1.jar"
echo [GET] No Chat Reports
curl -sL "https://cdn.modrinth.com/data/qQyHxfxd/versions/2yrLNE3S/NoChatReports-FABRIC-26.1-v2.19.0.jar" -o "mods\NoChatReports-FABRIC-26.1-v2.19.0.jar"

echo.
echo --- Map ---
echo [GET] JourneyMap
curl -sL "https://cdn.modrinth.com/data/lfHFW1mp/versions/1lcmIgq5/journeymap-fabric-26.1-6.0.0-beta.64.jar" -o "mods\journeymap-fabric-26.1-6.0.0-beta.64.jar"

echo.
echo --- Custom ---
if exist essentials\build\libs\essentials-0.1.0+26.1.jar (
    copy essentials\build\libs\essentials-0.1.0+26.1.jar mods\ >nul
    echo [COPY] Essentials
) else (
    echo [WARN] Essentials not built. Run: cd essentials ^&^& gradlew.bat build
)

echo.
echo === Done! ===
echo.
echo Not yet available for 26.1 (install when updated):
echo   - Detail Armor Bar
echo   - Pick Up Notifier
echo   - Easy Shulker Boxes
echo   - REI (Roughly Enough Items)
pause
