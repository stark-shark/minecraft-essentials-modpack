package com.essentials.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.jetbrains.annotations.Nullable;

public class ClientShulkerBoxTooltip implements ClientTooltipComponent {

    private static final int SLOT_SIZE = 18;
    private static final int GRID_COLS = 9;
    private static final int GRID_ROWS = 3;
    private static final int PADDING = 5;
    private static final int BORDER = 2;

    private final NonNullList<ItemStack> items;
    private final @Nullable DyeColor color;

    public ClientShulkerBoxTooltip(ItemContainerContents contents, @Nullable DyeColor color) {
        this.items = NonNullList.withSize(GRID_COLS * GRID_ROWS, ItemStack.EMPTY);
        contents.copyInto(this.items);
        this.color = color;
    }

    @Override
    public int getWidth(Font font) {
        return GRID_COLS * SLOT_SIZE + PADDING * 2;
    }

    @Override
    public int getHeight(Font font) {
        return GRID_ROWS * SLOT_SIZE + PADDING * 2;
    }

    @Override
    public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
        int borderColor = getBorderColor();
        int totalW = GRID_COLS * SLOT_SIZE + PADDING * 2;
        int totalH = GRID_ROWS * SLOT_SIZE + PADDING * 2;

        // Outer border
        graphics.fill(x, y, x + totalW, y + BORDER, borderColor);
        graphics.fill(x, y + totalH - BORDER, x + totalW, y + totalH, borderColor);
        graphics.fill(x, y, x + BORDER, y + totalH, borderColor);
        graphics.fill(x + totalW - BORDER, y, x + totalW, y + totalH, borderColor);

        // Inner background
        graphics.fill(x + BORDER, y + BORDER, x + totalW - BORDER, y + totalH - BORDER, 0xF0100010);

        int gridX = x + PADDING;
        int gridY = y + PADDING;

        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                int index = row * GRID_COLS + col;
                int slotX = gridX + col * SLOT_SIZE;
                int slotY = gridY + row * SLOT_SIZE;

                // Slot background with subtle bevel
                graphics.fill(slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, 0xFF8B8B8B);
                graphics.fill(slotX + 1, slotY + 1, slotX + SLOT_SIZE, slotY + SLOT_SIZE, 0xFF373737);
                graphics.fill(slotX + 1, slotY + 1, slotX + SLOT_SIZE - 1, slotY + SLOT_SIZE - 1, 0xFF555555);

                ItemStack stack = items.get(index);
                if (!stack.isEmpty()) {
                    graphics.item(stack, slotX + 1, slotY + 1);
                    graphics.itemDecorations(font, stack, slotX + 1, slotY + 1);
                }
            }
        }
    }

    private int getBorderColor() {
        if (color == null) return 0xFF8932B8; // Default purple for undyed shulker
        int rgb = color.getTextureDiffuseColor();
        // textureDiffuseColor is 0xRRGGBB — add alpha and brighten slightly
        int r = Math.min(255, ((rgb >> 16) & 0xFF) + 30);
        int g = Math.min(255, ((rgb >> 8) & 0xFF) + 30);
        int b = Math.min(255, (rgb & 0xFF) + 30);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
