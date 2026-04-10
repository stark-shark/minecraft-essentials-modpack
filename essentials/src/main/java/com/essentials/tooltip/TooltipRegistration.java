package com.essentials.tooltip;

import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;

public final class TooltipRegistration {

    public static void register() {
        ClientTooltipComponentCallback.EVENT.register(component -> {
            if (component instanceof ShulkerBoxContentsTooltip shulker) {
                return new ClientShulkerBoxTooltip(shulker.contents(), shulker.color());
            }
            if (component instanceof EnderChestContentsTooltip) {
                return new ClientEnderChestTooltip();
            }
            if (component instanceof MapPreviewTooltip map) {
                return new ClientMapPreviewTooltip(map.mapId());
            }
            return null; // Not ours — let other handlers try
        });
    }
}
