package com.essentials.statuseffect;

import com.essentials.config.EssentialsConfig;
import com.google.common.collect.Ordering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Draws a duration progress bar at the bottom of each status effect icon tile.
 * The bar starts full (100%) and drains as the effect expires.
 */
public class StatusEffectOverlay {

    // Track the max duration we've seen for each effect, so the bar starts at 100%
    private static final Map<Holder<MobEffect>, Integer> maxDurations = new HashMap<>();

    public static void renderBars(GuiGraphicsExtractor graphics, Minecraft mc) {
        if (!EssentialsConfig.statusEffectTimerEnabled) return;
        if (mc.player == null) return;

        Collection<MobEffectInstance> activeEffects = mc.player.getActiveEffects();
        if (activeEffects.isEmpty()) {
            maxDurations.clear();
            return;
        }

        int beneficialCount = 0;
        int harmfulCount = 0;

        for (MobEffectInstance instance : Ordering.natural().reverse().sortedCopy(activeEffects)) {
            if (!instance.showIcon()) continue;

            int x = graphics.guiWidth();
            int y = 1;
            if (mc.isDemo()) y += 15;

            if (instance.getEffect().value().isBeneficial()) {
                beneficialCount++;
                x -= 25 * beneficialCount;
            } else {
                harmfulCount++;
                x -= 25 * harmfulCount;
                y += 26;
            }

            if (instance.isInfiniteDuration()) continue;

            int currentDur = instance.getDuration();

            // Track the max duration — updates when the effect is first applied or refreshed
            Integer storedMax = maxDurations.get(instance.getEffect());
            if (storedMax == null || currentDur > storedMax) {
                maxDurations.put(instance.getEffect(), currentDur);
            }
            int maxDur = maxDurations.getOrDefault(instance.getEffect(), currentDur);
            if (maxDur <= 0) maxDur = 1;

            float progress = Math.min(1.0f, (float) currentDur / maxDur);

            // Bar at bottom of the 24x24 tile
            int barX = x + 1;
            int barY = y + 22;
            int barWidth = 22;
            int filledWidth = Math.max(1, Math.round(barWidth * progress));

            // Background
            graphics.fill(barX, barY, barX + barWidth, barY + 2, 0xC0000000);

            // Color based on remaining percentage
            int color;
            if (progress <= 0.15f) {
                color = 0xC0FF3333; // Red — almost gone
            } else if (progress <= 0.35f) {
                color = 0xC0FFAA00; // Orange
            } else {
                color = instance.getEffect().value().isBeneficial() ? 0xC055FF55 : 0xC0FF5555;
            }

            graphics.fill(barX, barY, barX + filledWidth, barY + 2, color);
        }

        // Clean up expired effects from tracking
        maxDurations.keySet().removeIf(effect ->
                activeEffects.stream().noneMatch(e -> e.getEffect().equals(effect)));
    }
}
