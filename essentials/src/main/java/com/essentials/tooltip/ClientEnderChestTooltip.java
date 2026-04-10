package com.essentials.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ClientEnderChestTooltip implements ClientTooltipComponent {

    private static final int SLOT_SIZE = 18;
    private static final int GRID_COLS = 9;
    private static final int GRID_ROWS = 3;
    private static final int PADDING = 5;
    private static final int BORDER = 2;
    private static final int HEADER_HEIGHT = 12;
    private static final int BORDER_COLOR = 0xFF1B8045; // Ender green

    @Override
    public int getWidth(Font font) {
        if (!EnderChestCache.hasData()) {
            return font.width("Open ender chest to view contents") + PADDING * 2;
        }
        return GRID_COLS * SLOT_SIZE + PADDING * 2;
    }

    @Override
    public int getHeight(Font font) {
        if (!EnderChestCache.hasData()) {
            return 12 + PADDING * 2;
        }
        return GRID_ROWS * SLOT_SIZE + PADDING * 2 + HEADER_HEIGHT;
    }

    @Override
    public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
        if (!EnderChestCache.hasData()) {
            graphics.fill(x, y, x + getWidth(font), y + getHeight(font), 0xF0100010);
            graphics.text(font, "Open ender chest to view contents", x + PADDING, y + PADDING, 0xFF888888);
            return;
        }

        List<ItemStack> items = EnderChestCache.getItems();

        int totalW = GRID_COLS * SLOT_SIZE + PADDING * 2;
        int totalH = GRID_ROWS * SLOT_SIZE + PADDING * 2 + HEADER_HEIGHT;

        // Outer border
        graphics.fill(x, y, x + totalW, y + BORDER, BORDER_COLOR);
        graphics.fill(x, y + totalH - BORDER, x + totalW, y + totalH, BORDER_COLOR);
        graphics.fill(x, y, x + BORDER, y + totalH, BORDER_COLOR);
        graphics.fill(x + totalW - BORDER, y, x + totalW, y + totalH, BORDER_COLOR);

        // Inner background
        graphics.fill(x + BORDER, y + BORDER, x + totalW - BORDER, y + totalH - BORDER, 0xF0100010);

        // Header label
        graphics.text(font, "Ender Chest", x + PADDING + 1, y + PADDING, 0xFF55FFAA);

        int gridX = x + PADDING;
        int gridY = y + PADDING + HEADER_HEIGHT;

        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                int index = row * GRID_COLS + col;
                int slotX = gridX + col * SLOT_SIZE;
                int slotY = gridY + row * SLOT_SIZE;

                // Slot background with bevel
                graphics.fill(slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, 0xFF8B8B8B);
                graphics.fill(slotX + 1, slotY + 1, slotX + SLOT_SIZE, slotY + SLOT_SIZE, 0xFF373737);
                graphics.fill(slotX + 1, slotY + 1, slotX + SLOT_SIZE - 1, slotY + SLOT_SIZE - 1, 0xFF555555);

                if (index < items.size()) {
                    ItemStack stack = items.get(index);
                    if (!stack.isEmpty()) {
                        graphics.item(stack, slotX + 1, slotY + 1);
                        graphics.itemDecorations(font, stack, slotX + 1, slotY + 1);
                    }
                }
            }
        }
    }
}
