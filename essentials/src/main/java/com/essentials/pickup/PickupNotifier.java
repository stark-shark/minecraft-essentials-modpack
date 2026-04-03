package com.essentials.pickup;

import com.essentials.config.EssentialsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PickupNotifier {

    private static final int MAX_ENTRIES = 8;
    private static final long DISPLAY_TIME_MS = 5000;
    private static final long FADE_TIME_MS = 500;

    private static final List<PickupEntry> entries = new ArrayList<>();

    public static void onItemPickedUp(ItemStack stack, int count) {
        if (!EssentialsConfig.pickupNotifierEnabled || stack.isEmpty() || count <= 0) return;

        String itemName = stack.getHoverName().getString();

        for (PickupEntry entry : entries) {
            if (entry.itemName.equals(itemName)) {
                entry.count += count;
                entry.timestamp = System.currentTimeMillis();
                return;
            }
        }

        if (entries.size() >= MAX_ENTRIES) {
            entries.remove(0);
        }
        entries.add(new PickupEntry(itemName, count, stack.copy()));
    }

    public static void render(GuiGraphicsExtractor graphics, Minecraft mc) {
        if (!EssentialsConfig.pickupNotifierEnabled || entries.isEmpty()) return;

        long now = System.currentTimeMillis();
        Font font = mc.font;
        int guiWidth = graphics.guiWidth();
        int guiHeight = graphics.guiHeight();

        Iterator<PickupEntry> it = entries.iterator();
        while (it.hasNext()) {
            PickupEntry entry = it.next();
            if (now - entry.timestamp > DISPLAY_TIME_MS + FADE_TIME_MS) {
                it.remove();
            }
        }

        float scale = EssentialsConfig.pickupNotifierScale;

        // The lowest point of the notifier stays at guiHeight - 20
        // We render in scaled space, so convert the anchor point
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.scale(scale, scale);

        int scaledGuiWidth = (int) (guiWidth / scale);
        int scaledGuiHeight = (int) (guiHeight / scale);
        int anchorY = scaledGuiHeight - (int)(20 / scale);
        int lineSpacing = 18; // Constant in render space — scales with matrix (same as durability)

        int y = anchorY;

        for (int i = entries.size() - 1; i >= 0; i--) {
            PickupEntry entry = entries.get(i);
            long age = now - entry.timestamp;

            int alpha;
            if (age > DISPLAY_TIME_MS) {
                float fadeProgress = (age - DISPLAY_TIME_MS) / (float) FADE_TIME_MS;
                alpha = (int) (255 * (1.0f - fadeProgress));
            } else {
                alpha = 255;
            }
            if (alpha <= 4) continue;

            String text = entry.count + "x " + entry.itemName;
            int textWidth = font.width(text);

            int textX = scaledGuiWidth - 10 - textWidth;
            int iconX = textX - 18;

            // Item icon
            graphics.item(entry.stack, iconX, y);

            // Text
            int textColor = (alpha << 24) | 0xFFFFFF;
            graphics.text(font, text, textX, y + 4, textColor, true);

            y -= lineSpacing;
        }

        pose.popMatrix();
    }

    private static class PickupEntry {
        String itemName;
        int count;
        ItemStack stack;
        long timestamp;

        PickupEntry(String itemName, int count, ItemStack stack) {
            this.itemName = itemName;
            this.count = count;
            this.stack = stack;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
