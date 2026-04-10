package com.essentials.config;

import com.essentials.EssentialsMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class EssentialsConfig {

    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("essentials.properties");

    // --- Feature toggles ---
    public static boolean emissiveEnabled = true;
    public static boolean emissiveBlocks = true;
    public static boolean emissiveEntities = true;

    public static boolean zoomEnabled = true;
    public static float zoomDefaultFov = 30.0f;
    public static float zoomScrollSensitivity = 5.0f;
    public static float zoomTransitionSpeed = 0.15f;
    public static boolean zoomReduceSensitivity = true;
    public static boolean zoomSmoothCamera = false;
    public static float zoomMinFov = 5.0f;
    public static float zoomMaxFov = 70.0f;

    public static boolean fullbrightEnabled = true;
    public static boolean fullbrightActive = false;

    public static boolean fogEnabled = true;
    public static boolean fogDisableVoid = true;
    public static boolean fogDisableWater = true;
    public static boolean fogDisableLava = false;
    public static boolean fogDisableDistance = false;
    public static float fogDistanceMultiplier = 1.0f;

    public static boolean dynamicFpsEnabled = true;
    public static int dynamicFpsUnfocusedCap = 15;
    public static int dynamicFpsMinimizedCap = 1;

    public static boolean borderlessEnabled = true;

    public static boolean healthBarsEnabled = true;
    public static boolean healthBarsMobs = true;
    public static boolean healthBarsMobsFull = false;
    public static boolean healthBarsPlayers = true;
    public static boolean healthBarsPlayersFull = false;

    public static boolean statusEffectTimerEnabled = true;

    public static boolean coordsHudEnabled = false;
    public static float coordsHudScale = 1.0f;
    public static boolean deathWaypointEnabled = true;
    public static boolean durabilityHudEnabled = true;
    public static float durabilityHudScale = 0.5f;
    public static boolean pickupNotifierEnabled = true;
    public static float pickupNotifierScale = 0.75f;

    public static boolean autoClickEnabled = true;
    public static int autoClickMode = 0;           // 0 = hold, 1 = click interval
    public static int autoClickButton = 0;         // 0 = left click, 1 = right click
    public static int autoClickCPS = 5;            // clicks per second (1-20)

    public static boolean shulkerTooltipEnabled = true;
    public static boolean enderChestTooltipEnabled = true;
    public static boolean mapTooltipEnabled = true;
    public static boolean enhancedTooltipsEnabled = true;
    public static boolean showToolStats = true;
    public static boolean showWeaponStats = true;
    public static boolean showFoodStats = true;

    // Per-item-type stat modes: 0 = tool only, 1 = weapon only, 2 = both
    public static int pickaxeStatMode = 0;  // Tool only
    public static int axeStatMode = 2;      // Both
    public static int shovelStatMode = 0;   // Tool only
    public static int hoeStatMode = 0;      // Tool only
    public static int swordStatMode = 1;    // Weapon only
    public static int maceStatMode = 1;     // Weapon only

    public static boolean hotbarRefillEnabled = true;
    public static boolean refillOnDrop = false;
    public static boolean refillMatchTypeOnly = true; // true = any diamond pick matches any diamond pick
    public static boolean refillBlocks = true;
    public static boolean refillTools = true;
    public static boolean refillWeapons = true;
    public static boolean refillFood = true;
    public static boolean refillContainers = true;
    public static boolean refillPotions = true;
    public static boolean refillProjectiles = true;
    public static boolean refillOther = true;

    public static boolean inventorySortEnabled = true;

    public static void init() {
        if (Files.exists(CONFIG_PATH)) {
            load();
        } else {
            save();
        }
    }

    public static void load() {
        try {
            Properties props = new Properties();
            props.load(Files.newInputStream(CONFIG_PATH));

            emissiveEnabled = getBool(props, "emissive.enabled", emissiveEnabled);
            emissiveBlocks = getBool(props, "emissive.blocks", emissiveBlocks);
            emissiveEntities = getBool(props, "emissive.entities", emissiveEntities);

            zoomEnabled = getBool(props, "zoom.enabled", zoomEnabled);
            zoomDefaultFov = getFloat(props, "zoom.defaultFov", zoomDefaultFov);
            zoomScrollSensitivity = getFloat(props, "zoom.scrollSensitivity", zoomScrollSensitivity);
            zoomTransitionSpeed = getFloat(props, "zoom.transitionSpeed", zoomTransitionSpeed);
            zoomReduceSensitivity = getBool(props, "zoom.reduceSensitivity", zoomReduceSensitivity);
            zoomSmoothCamera = getBool(props, "zoom.smoothCamera", zoomSmoothCamera);

            fullbrightEnabled = getBool(props, "fullbright.enabled", fullbrightEnabled);
            fullbrightActive = getBool(props, "fullbright.active", fullbrightActive);

            fogEnabled = getBool(props, "fog.enabled", fogEnabled);
            fogDisableVoid = getBool(props, "fog.disableVoid", fogDisableVoid);
            fogDisableWater = getBool(props, "fog.disableWater", fogDisableWater);
            fogDisableLava = getBool(props, "fog.disableLava", fogDisableLava);
            fogDisableDistance = getBool(props, "fog.disableDistance", fogDisableDistance);
            fogDistanceMultiplier = getFloat(props, "fog.distanceMultiplier", fogDistanceMultiplier);

            dynamicFpsEnabled = getBool(props, "dynamicfps.enabled", dynamicFpsEnabled);
            dynamicFpsUnfocusedCap = getInt(props, "dynamicfps.unfocusedCap", dynamicFpsUnfocusedCap);
            dynamicFpsMinimizedCap = getInt(props, "dynamicfps.minimizedCap", dynamicFpsMinimizedCap);

            borderlessEnabled = getBool(props, "borderless.enabled", borderlessEnabled);

            healthBarsEnabled = getBool(props, "healthbars.enabled", healthBarsEnabled);
            healthBarsMobs = getBool(props, "healthbars.mobs", healthBarsMobs);
            healthBarsMobsFull = getBool(props, "healthbars.mobsFull", healthBarsMobsFull);
            healthBarsPlayers = getBool(props, "healthbars.players", healthBarsPlayers);
            healthBarsPlayersFull = getBool(props, "healthbars.playersFull", healthBarsPlayersFull);

            statusEffectTimerEnabled = getBool(props, "statuseffect.timer", statusEffectTimerEnabled);

            coordsHudEnabled = getBool(props, "coordshud.enabled", coordsHudEnabled);
            coordsHudScale = getFloat(props, "coordshud.scale", coordsHudScale);
            deathWaypointEnabled = getBool(props, "deathwaypoint.enabled", deathWaypointEnabled);
            durabilityHudEnabled = getBool(props, "durabilityhud.enabled", durabilityHudEnabled);
            durabilityHudScale = getFloat(props, "durabilityhud.scale", durabilityHudScale);
            pickupNotifierEnabled = getBool(props, "pickup.enabled", pickupNotifierEnabled);
            pickupNotifierScale = getFloat(props, "pickup.scale", pickupNotifierScale);

            autoClickEnabled = getBool(props, "autoclick.enabled", autoClickEnabled);
            autoClickMode = getInt(props, "autoclick.mode", autoClickMode);
            autoClickButton = getInt(props, "autoclick.button", autoClickButton);
            autoClickCPS = Math.clamp(getInt(props, "autoclick.cps", autoClickCPS), 1, 20);

            shulkerTooltipEnabled = getBool(props, "tooltip.shulker", shulkerTooltipEnabled);
            enderChestTooltipEnabled = getBool(props, "tooltip.enderchest", enderChestTooltipEnabled);
            mapTooltipEnabled = getBool(props, "tooltip.map", mapTooltipEnabled);
            enhancedTooltipsEnabled = getBool(props, "tooltip.enhanced", enhancedTooltipsEnabled);
            showToolStats = getBool(props, "tooltip.toolStats", showToolStats);
            showWeaponStats = getBool(props, "tooltip.weaponStats", showWeaponStats);
            showFoodStats = getBool(props, "tooltip.foodStats", showFoodStats);
            pickaxeStatMode = getInt(props, "tooltip.stats.pickaxe", pickaxeStatMode);
            axeStatMode = getInt(props, "tooltip.stats.axe", axeStatMode);
            shovelStatMode = getInt(props, "tooltip.stats.shovel", shovelStatMode);
            hoeStatMode = getInt(props, "tooltip.stats.hoe", hoeStatMode);
            swordStatMode = getInt(props, "tooltip.stats.sword", swordStatMode);
            maceStatMode = getInt(props, "tooltip.stats.mace", maceStatMode);

            hotbarRefillEnabled = getBool(props, "inventory.hotbarRefill", hotbarRefillEnabled);
            refillOnDrop = getBool(props, "inventory.refill.onDrop", refillOnDrop);
            refillMatchTypeOnly = getBool(props, "inventory.refill.matchTypeOnly", refillMatchTypeOnly);
            refillBlocks = getBool(props, "inventory.refill.blocks", refillBlocks);
            refillTools = getBool(props, "inventory.refill.tools", refillTools);
            refillWeapons = getBool(props, "inventory.refill.weapons", refillWeapons);
            refillFood = getBool(props, "inventory.refill.food", refillFood);
            refillContainers = getBool(props, "inventory.refill.containers", refillContainers);
            refillPotions = getBool(props, "inventory.refill.potions", refillPotions);
            refillProjectiles = getBool(props, "inventory.refill.projectiles", refillProjectiles);
            refillOther = getBool(props, "inventory.refill.other", refillOther);
            inventorySortEnabled = getBool(props, "inventory.sort", inventorySortEnabled);

            EssentialsMod.LOGGER.info("[Essentials] Config loaded.");
        } catch (IOException e) {
            EssentialsMod.LOGGER.error("[Essentials] Failed to load config", e);
        }
    }

    public static void save() {
        try {
            Properties props = new Properties();

            props.setProperty("emissive.enabled", String.valueOf(emissiveEnabled));
            props.setProperty("emissive.blocks", String.valueOf(emissiveBlocks));
            props.setProperty("emissive.entities", String.valueOf(emissiveEntities));

            props.setProperty("zoom.enabled", String.valueOf(zoomEnabled));
            props.setProperty("zoom.defaultFov", String.valueOf(zoomDefaultFov));
            props.setProperty("zoom.scrollSensitivity", String.valueOf(zoomScrollSensitivity));
            props.setProperty("zoom.transitionSpeed", String.valueOf(zoomTransitionSpeed));
            props.setProperty("zoom.reduceSensitivity", String.valueOf(zoomReduceSensitivity));
            props.setProperty("zoom.smoothCamera", String.valueOf(zoomSmoothCamera));

            props.setProperty("fullbright.enabled", String.valueOf(fullbrightEnabled));
            props.setProperty("fullbright.active", String.valueOf(fullbrightActive));

            props.setProperty("fog.enabled", String.valueOf(fogEnabled));
            props.setProperty("fog.disableVoid", String.valueOf(fogDisableVoid));
            props.setProperty("fog.disableWater", String.valueOf(fogDisableWater));
            props.setProperty("fog.disableLava", String.valueOf(fogDisableLava));
            props.setProperty("fog.disableDistance", String.valueOf(fogDisableDistance));
            props.setProperty("fog.distanceMultiplier", String.valueOf(fogDistanceMultiplier));

            props.setProperty("dynamicfps.enabled", String.valueOf(dynamicFpsEnabled));
            props.setProperty("dynamicfps.unfocusedCap", String.valueOf(dynamicFpsUnfocusedCap));
            props.setProperty("dynamicfps.minimizedCap", String.valueOf(dynamicFpsMinimizedCap));

            props.setProperty("borderless.enabled", String.valueOf(borderlessEnabled));

            props.setProperty("healthbars.enabled", String.valueOf(healthBarsEnabled));
            props.setProperty("healthbars.mobs", String.valueOf(healthBarsMobs));
            props.setProperty("healthbars.mobsFull", String.valueOf(healthBarsMobsFull));
            props.setProperty("healthbars.players", String.valueOf(healthBarsPlayers));
            props.setProperty("healthbars.playersFull", String.valueOf(healthBarsPlayersFull));

            props.setProperty("statuseffect.timer", String.valueOf(statusEffectTimerEnabled));

            props.setProperty("coordshud.enabled", String.valueOf(coordsHudEnabled));
            props.setProperty("coordshud.scale", String.valueOf(coordsHudScale));
            props.setProperty("deathwaypoint.enabled", String.valueOf(deathWaypointEnabled));
            props.setProperty("durabilityhud.enabled", String.valueOf(durabilityHudEnabled));
            props.setProperty("durabilityhud.scale", String.valueOf(durabilityHudScale));
            props.setProperty("pickup.enabled", String.valueOf(pickupNotifierEnabled));
            props.setProperty("pickup.scale", String.valueOf(pickupNotifierScale));

            props.setProperty("autoclick.enabled", String.valueOf(autoClickEnabled));
            props.setProperty("autoclick.mode", String.valueOf(autoClickMode));
            props.setProperty("autoclick.button", String.valueOf(autoClickButton));
            props.setProperty("autoclick.cps", String.valueOf(autoClickCPS));

            props.setProperty("tooltip.shulker", String.valueOf(shulkerTooltipEnabled));
            props.setProperty("tooltip.enderchest", String.valueOf(enderChestTooltipEnabled));
            props.setProperty("tooltip.map", String.valueOf(mapTooltipEnabled));
            props.setProperty("tooltip.enhanced", String.valueOf(enhancedTooltipsEnabled));
            props.setProperty("tooltip.toolStats", String.valueOf(showToolStats));
            props.setProperty("tooltip.weaponStats", String.valueOf(showWeaponStats));
            props.setProperty("tooltip.foodStats", String.valueOf(showFoodStats));
            props.setProperty("tooltip.stats.pickaxe", String.valueOf(pickaxeStatMode));
            props.setProperty("tooltip.stats.axe", String.valueOf(axeStatMode));
            props.setProperty("tooltip.stats.shovel", String.valueOf(shovelStatMode));
            props.setProperty("tooltip.stats.hoe", String.valueOf(hoeStatMode));
            props.setProperty("tooltip.stats.sword", String.valueOf(swordStatMode));
            props.setProperty("tooltip.stats.mace", String.valueOf(maceStatMode));

            props.setProperty("inventory.hotbarRefill", String.valueOf(hotbarRefillEnabled));
            props.setProperty("inventory.refill.onDrop", String.valueOf(refillOnDrop));
            props.setProperty("inventory.refill.matchTypeOnly", String.valueOf(refillMatchTypeOnly));
            props.setProperty("inventory.refill.blocks", String.valueOf(refillBlocks));
            props.setProperty("inventory.refill.tools", String.valueOf(refillTools));
            props.setProperty("inventory.refill.weapons", String.valueOf(refillWeapons));
            props.setProperty("inventory.refill.food", String.valueOf(refillFood));
            props.setProperty("inventory.refill.containers", String.valueOf(refillContainers));
            props.setProperty("inventory.refill.potions", String.valueOf(refillPotions));
            props.setProperty("inventory.refill.projectiles", String.valueOf(refillProjectiles));
            props.setProperty("inventory.refill.other", String.valueOf(refillOther));
            props.setProperty("inventory.sort", String.valueOf(inventorySortEnabled));

            props.store(Files.newOutputStream(CONFIG_PATH), "Essentials Mod Config");
            EssentialsMod.LOGGER.info("[Essentials] Config saved.");
        } catch (IOException e) {
            EssentialsMod.LOGGER.error("[Essentials] Failed to save config", e);
        }
    }

    private static boolean getBool(Properties props, String key, boolean def) {
        String val = props.getProperty(key);
        return val != null ? Boolean.parseBoolean(val) : def;
    }

    private static float getFloat(Properties props, String key, float def) {
        String val = props.getProperty(key);
        if (val == null) return def;
        try { return Float.parseFloat(val); } catch (NumberFormatException e) { return def; }
    }

    private static int getInt(Properties props, String key, int def) {
        String val = props.getProperty(key);
        if (val == null) return def;
        try { return Integer.parseInt(val); } catch (NumberFormatException e) { return def; }
    }
}
