package com.essentials.autoclicker;

import com.essentials.config.EssentialsConfig;
import com.essentials.keybind.ModKeybinds;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class AutoClickHandler {

    private static boolean active = false;
    private static int tickCounter = 0;

    public static boolean isActive() { return active; }

    public static void tick(Minecraft mc) {
        if (!EssentialsConfig.autoClickEnabled) {
            if (active) { getKey(mc).setDown(false); active = false; }
            return;
        }

        if (mc.player == null || mc.level == null) { active = false; return; }
        if (mc.screen != null) {
            if (active) getKey(mc).setDown(false);
            return;
        }

        if (ModKeybinds.autoAttackKey.consumeClick()) {
            active = !active;
            if (!active) getKey(mc).setDown(false);
        }

        if (active) {
            KeyMapping key = getKey(mc);
            if (EssentialsConfig.autoClickMode == 0) {
                // Hold mode
                key.setDown(true);
            } else {
                // Click mode
                tickCounter++;
                int interval = Math.max(1, 20 / Math.max(1, EssentialsConfig.autoClickCPS));
                if (tickCounter >= interval) {
                    tickCounter = 0;
                    key.setDown(true);
                } else {
                    key.setDown(false);
                }
            }
        }
    }

    private static KeyMapping getKey(Minecraft mc) {
        return EssentialsConfig.autoClickButton == 0 ? mc.options.keyAttack : mc.options.keyUse;
    }
}
