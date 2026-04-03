#!/bin/bash
# Downloads all modpack mods from Modrinth for MC 26.1 Fabric
# Run from the modpack root directory

set -e
MODS_DIR="mods"
mkdir -p "$MODS_DIR"

echo "=== Essentials Modpack — Downloading mods for MC 26.1 ==="
echo ""

download() {
    local name="$1"
    local url="$2"
    local filename=$(basename "$url" | sed 's/%2B/+/g')

    if [ -f "$MODS_DIR/$filename" ]; then
        echo "[SKIP] $name — already downloaded"
    else
        echo "[GET]  $name"
        curl -sL "$url" -o "$MODS_DIR/$filename"
        echo "       -> $filename"
    fi
}

echo "--- Dependencies ---"
download "Fabric API"          "https://cdn.modrinth.com/data/P7dR8mSH/versions/G0yfY6x2/fabric-api-0.145.3%2B26.1.1.jar"
download "Cloth Config"        "https://cdn.modrinth.com/data/9s6osm5g/versions/GFM8zh9J/cloth-config-26.1.154.jar"
download "Mod Menu"            "https://cdn.modrinth.com/data/mOgUt4GM/versions/jvjwXH6l/modmenu-18.0.0-alpha.8.jar"

echo ""
echo "--- Performance ---"
download "Sodium"              "https://cdn.modrinth.com/data/AANobbMI/versions/Amr4VcZo/sodium-fabric-0.8.7%2Bmc26.1.jar"
download "Lithium"             "https://cdn.modrinth.com/data/gvQqBUqZ/versions/W0ZXKJy9/lithium-fabric-0.22.1%2Bmc26.1.jar"
download "Entity Culling"      "https://cdn.modrinth.com/data/NNAgCjsB/versions/YSbzFHRt/entityculling-fabric-1.10.0-mc26.1.jar"

echo ""
echo "--- Visual ---"
download "Iris Shaders"        "https://cdn.modrinth.com/data/YL57xq9U/versions/Yi5E3d2l/iris-fabric-1.10.8%2Bmc26.1.jar"
download "LambDynamicLights"   "https://cdn.modrinth.com/data/yBW8D80W/versions/Nttq3ROe/lambdynamiclights-4.10.0%2B26.1.jar"
download "Status Effect Bars"  "https://cdn.modrinth.com/data/x02cBj9Y/versions/HPrO2zBi/status-effect-bars-1.0.11.jar"
download "Chat Heads"          "https://cdn.modrinth.com/data/Wb5oqrBJ/versions/J5xd8lnJ/chat_heads-1.2.2-fabric-26.1.jar"

echo ""
echo "--- Quality of Life ---"
download "Mouse Tweaks"        "https://cdn.modrinth.com/data/aC3cM3Vq/versions/EBIKCzuP/MouseTweaks-fabric-mc26.1-2.31.jar"
download "Controlling"         "https://cdn.modrinth.com/data/xv94TkTM/versions/2rMoGipz/Controlling-fabric-26.1-26.1.0.1.jar"
download "No Chat Reports"    "https://cdn.modrinth.com/data/qQyHxfxd/versions/2yrLNE3S/NoChatReports-FABRIC-26.1-v2.19.0.jar"

echo ""
echo "--- Map ---"
download "JourneyMap"          "https://cdn.modrinth.com/data/lfHFW1mp/versions/1lcmIgq5/journeymap-fabric-26.1-6.0.0-beta.64.jar"

echo ""
echo "--- Custom ---"
if [ -f "essentials/build/libs/essentials-0.1.0+26.1.jar" ]; then
    cp "essentials/build/libs/essentials-0.1.0+26.1.jar" "$MODS_DIR/"
    echo "[COPY] Essentials (from build)"
elif [ -f "$MODS_DIR/essentials-0.1.0+26.1.jar" ]; then
    echo "[SKIP] Essentials — already present"
else
    echo "[WARN] Essentials not built yet. Run: cd essentials && ./gradlew build"
fi

echo ""
echo "=== Done! $(ls -1 "$MODS_DIR"/*.jar 2>/dev/null | wc -l) mods in $MODS_DIR/ ==="
echo ""
echo "Not yet available for 26.1 (install when updated):"
echo "  - Detail Armor Bar"
echo "  - Pick Up Notifier"
echo "  - Easy Shulker Boxes"
echo "  - REI (Roughly Enough Items)"
