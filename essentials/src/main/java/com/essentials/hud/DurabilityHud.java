package com.essentials.hud;

import com.essentials.config.EssentialsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;

public class DurabilityHud {

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public static void render(GuiGraphicsExtractor graphics, Minecraft mc) {
        if (!EssentialsConfig.durabilityHudEnabled) return;
        if (mc.player == null || mc.options.hideGui) return;
        if (mc.screen != null) return;

        float scale = EssentialsConfig.durabilityHudScale;
        // Position below coords HUD — account for coords scale
        int startY;
        if (EssentialsConfig.coordsHudEnabled) {
            startY = (int)(4 + 14 * EssentialsConfig.coordsHudScale) + 2;
        } else {
            startY = 4;
        }
        int x = 4;

        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.scale(scale, scale);

        // Render in scaled space — icon is always 16px in render space
        // Spacing = 18px in render space (16px icon + 2px gap)
        int scaledX = (int) (x / scale);
        int scaledStartY = (int) (startY / scale);
        int spacing = 18; // Constant in render space — scales with the matrix

        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack armor = mc.player.getItemBySlot(slot);
            if (armor.isEmpty()) continue;

            // Item icon
            graphics.item(armor, scaledX, scaledStartY);

            // Durability bar
            if (armor.isDamageableItem() && armor.getMaxDamage() > 0) {
                float durability = 1.0f - (float) armor.getDamageValue() / armor.getMaxDamage();
                renderDurabilityBar(graphics, scaledX + 18, scaledStartY + 4, 20, 2, durability);
            }

            scaledStartY += spacing;
        }

        pose.popMatrix();
    }

    private static void renderDurabilityBar(GuiGraphicsExtractor graphics, int x, int y, int width, int height, float durability) {
        graphics.fill(x, y, x + width, y + height, 0xFF222222);

        int color;
        if (durability > 0.6f) color = 0xFF55FF55;
        else if (durability > 0.3f) color = 0xFFFFFF55;
        else if (durability > 0.1f) color = 0xFFFF5555;
        else color = (System.currentTimeMillis() / 250 % 2 == 0) ? 0xFFFF0000 : 0xFFAA0000;

        int fillWidth = Math.max(1, Math.round(width * durability));
        graphics.fill(x, y, x + fillWidth, y + height, color);
    }
}
