package com.essentials.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class ClientMapPreviewTooltip implements ClientTooltipComponent {

    private static final int MAP_DISPLAY_SIZE = 128;
    private static final int PADDING = 4;
    private static final int BORDER = 2;
    private static final int BORDER_COLOR = 0xFF8B6F47; // Parchment brown
    private static final int HEADER_HEIGHT = 12;

    private final MapId mapId;

    public ClientMapPreviewTooltip(MapId mapId) {
        this.mapId = mapId;
    }

    @Override
    public int getWidth(Font font) {
        return MAP_DISPLAY_SIZE + PADDING * 2;
    }

    @Override
    public int getHeight(Font font) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return HEADER_HEIGHT + PADDING * 2;

        MapItemSavedData mapData = mc.level.getMapData(mapId);
        if (mapData == null) return HEADER_HEIGHT + PADDING * 2;

        return MAP_DISPLAY_SIZE + PADDING * 2 + HEADER_HEIGHT;
    }

    @Override
    public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        MapItemSavedData mapData = mc.level.getMapData(mapId);

        int totalW = MAP_DISPLAY_SIZE + PADDING * 2;
        int contentH;
        if (mapData != null) {
            contentH = MAP_DISPLAY_SIZE + PADDING * 2 + HEADER_HEIGHT;
        } else {
            contentH = HEADER_HEIGHT + PADDING * 2;
        }

        // Outer border
        graphics.fill(x, y, x + totalW, y + BORDER, BORDER_COLOR);
        graphics.fill(x, y + contentH - BORDER, x + totalW, y + contentH, BORDER_COLOR);
        graphics.fill(x, y, x + BORDER, y + contentH, BORDER_COLOR);
        graphics.fill(x + totalW - BORDER, y, x + totalW, y + contentH, BORDER_COLOR);

        // Inner background
        graphics.fill(x + BORDER, y + BORDER, x + totalW - BORDER, y + contentH - BORDER, 0xF0100010);

        // Header
        String label = "Map #" + mapId.id();
        graphics.text(font, label, x + PADDING + 1, y + PADDING, 0xFFD4A574);

        if (mapData == null) return;

        // Render map pixel data
        int mapX = x + PADDING;
        int mapY = y + PADDING + HEADER_HEIGHT;
        byte[] colors = mapData.colors;

        // Render 128x128 map at 1:1 pixel scale using fill() calls
        // To avoid 16384 individual fill calls, batch into horizontal runs of the same color
        for (int py = 0; py < 128; py++) {
            int runStart = 0;
            int runColor = getPixelColor(colors, 0, py);

            for (int px = 1; px <= 128; px++) {
                int pixelColor = (px < 128) ? getPixelColor(colors, px, py) : -1;
                if (pixelColor != runColor) {
                    // Flush run
                    if (runColor != 0) { // Skip fully transparent pixels
                        graphics.fill(
                                mapX + runStart, mapY + py,
                                mapX + px, mapY + py + 1,
                                runColor
                        );
                    }
                    runStart = px;
                    runColor = pixelColor;
                }
            }
        }
    }

    private static int getPixelColor(byte[] colors, int px, int py) {
        int packed = colors[px + py * 128] & 0xFF;
        if (packed == 0) return 0; // Transparent / unexplored
        return MapColor.getColorFromPackedId(packed);
    }
}
