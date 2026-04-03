package com.essentials.emissive;

import com.essentials.EssentialsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Scans resource packs for _e.png emissive texture overlays and maintains
 * a mapping from base texture ID to emissive texture ID.
 */
public class EmissiveTextureRegistry {

    public static final String EMISSIVE_SUFFIX = "_e";

    // Maps base texture identifier -> emissive texture identifier
    // e.g. "minecraft:block/diamond_ore" -> "minecraft:block/diamond_ore_e"
    private static final Map<Identifier, Identifier> emissiveMap = new HashMap<>();

    private static boolean scanned = false;

    public static Map<Identifier, Identifier> getEmissiveMap() {
        return emissiveMap;
    }

    public static boolean hasEmissive(Identifier baseTexture) {
        return emissiveMap.containsKey(baseTexture);
    }

    public static Identifier getEmissive(Identifier baseTexture) {
        return emissiveMap.get(baseTexture);
    }

    /**
     * Scan all loaded resource packs for _e.png textures.
     * Should be called during resource reload.
     */
    public static void scan(ResourceManager resourceManager) {
        emissiveMap.clear();

        // Scan for block textures with _e suffix
        Map<Identifier, Resource> allTextures = resourceManager.listResources(
                "textures", id -> id.getPath().endsWith(".png"));

        int count = 0;
        for (Identifier texId : allTextures.keySet()) {
            String path = texId.getPath();
            // Check if this is an emissive texture (ends with _e.png)
            if (path.endsWith(EMISSIVE_SUFFIX + ".png")) {
                // Derive the base texture ID
                String basePath = path.substring(0, path.length() - (EMISSIVE_SUFFIX + ".png").length()) + ".png";
                Identifier baseId = Identifier.fromNamespaceAndPath(texId.getNamespace(), basePath);

                // Only register if the base texture also exists
                if (allTextures.containsKey(baseId)) {
                    // Convert from file path to sprite ID
                    // "textures/block/diamond_ore.png" -> "block/diamond_ore"
                    String baseSpritePath = basePath.replace("textures/", "").replace(".png", "");
                    String emissiveSpritePath = path.replace("textures/", "").replace(".png", "");

                    Identifier baseSpriteId = Identifier.fromNamespaceAndPath(texId.getNamespace(), baseSpritePath);
                    Identifier emissiveSpriteId = Identifier.fromNamespaceAndPath(texId.getNamespace(), emissiveSpritePath);

                    emissiveMap.put(baseSpriteId, emissiveSpriteId);
                    count++;
                }
            }
        }

        scanned = true;
        if (count > 0) {
            EssentialsMod.LOGGER.info("[Essentials] Found {} emissive texture overlays.", count);
            // Log first few mappings for debugging
            int logged = 0;
            for (Map.Entry<Identifier, Identifier> entry : emissiveMap.entrySet()) {
                if (logged++ >= 5) break;
                EssentialsMod.LOGGER.info("[Essentials]   {} -> {}", entry.getKey(), entry.getValue());
            }
        }
    }

    public static boolean isScanned() {
        return scanned;
    }
}
