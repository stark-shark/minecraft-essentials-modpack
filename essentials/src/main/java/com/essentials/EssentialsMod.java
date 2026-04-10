package com.essentials;

import com.essentials.config.EssentialsConfig;
import com.essentials.emissive.EmissiveModelLoadingPlugin;
import com.essentials.inventory.HotbarRefillHandler;
import com.essentials.keybind.ModKeybinds;
import com.essentials.tooltip.EnderChestCache;
import com.essentials.tooltip.EnhancedItemTooltips;
import com.essentials.tooltip.TooltipRegistration;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EssentialsMod implements ClientModInitializer {

    public static final String MOD_ID = "essentials";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("[Essentials] Initializing...");

        EssentialsConfig.init();
        ModKeybinds.register();
        EmissiveModelLoadingPlugin.register();
        TooltipRegistration.register();
        EnhancedItemTooltips.register();
        EnderChestCache.register();
        HotbarRefillHandler.register();
        detectCompatibility();

        LOGGER.info("[Essentials] Loaded successfully.");
    }

    private void detectCompatibility() {
        FabricLoader loader = FabricLoader.getInstance();

        if (loader.isModLoaded("continuity")) {
            LOGGER.info("[Essentials] Detected Continuity — disabling emissive overlays to avoid conflict.");
            EssentialsConfig.emissiveEnabled = false;
        }
        if (loader.isModLoaded("dynamic-fps")) {
            LOGGER.info("[Essentials] Detected Dynamic FPS — disabling built-in dynamic FPS to avoid conflict.");
            EssentialsConfig.dynamicFpsEnabled = false;
        }
        if (loader.isModLoaded("zoomify") || loader.isModLoaded("ok-zoomer")) {
            LOGGER.info("[Essentials] Detected zoom mod — disabling built-in zoom to avoid conflict.");
            EssentialsConfig.zoomEnabled = false;
        }
        if (loader.isModLoaded("borderlesswindow")) {
            LOGGER.info("[Essentials] Detected borderless window mod — disabling built-in borderless to avoid conflict.");
            EssentialsConfig.borderlessEnabled = false;
        }
        if (loader.isModLoaded("easyshulkerboxes") || loader.isModLoaded("shulkerboxtooltip")) {
            LOGGER.info("[Essentials] Detected shulker tooltip mod — disabling built-in container tooltips to avoid conflict.");
            EssentialsConfig.shulkerTooltipEnabled = false;
            EssentialsConfig.enderChestTooltipEnabled = false;
            EssentialsConfig.mapTooltipEnabled = false;
        }
        // Debug HUD removed — use BetterF3 mod instead
    }
}
