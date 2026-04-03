package com.essentials.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {

    public static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("essentials", "main"));

    public static KeyMapping zoomKey;
    public static KeyMapping fullbrightKey;
    public static KeyMapping autoAttackKey;

    public static void register() {
        zoomKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.essentials.zoom",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                CATEGORY
        ));

        fullbrightKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.essentials.fullbright",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                CATEGORY
        ));

        autoAttackKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.essentials.autoattack",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT,
                CATEGORY
        ));
    }
}
