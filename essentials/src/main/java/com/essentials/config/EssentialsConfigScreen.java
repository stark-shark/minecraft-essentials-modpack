package com.essentials.config;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EssentialsConfigScreen extends Screen {

    private static final int SIDEBAR_WIDTH = 120;
    private static final int PADDING = 4;

    private final Screen parent;
    private String searchQuery = "";
    private String selectedCategory = "Emissive Textures";
    private EditBox searchBox;
    private CategoryList categoryList;
    private SettingsPanel settingsPanel;

    // Screen-level tooltip state (rendered on top of everything)
    private String pendingTooltip = null;
    private int pendingTooltipX = 0;
    private int pendingTooltipY = 0;

    private final Map<String, List<SettingEntry>> categories = new LinkedHashMap<>();

    public EssentialsConfigScreen(Screen parent) {
        super(Component.translatable("config.essentials.title"));
        this.parent = parent;
        buildCategories();
    }

    public static Screen create(Screen parent) {
        return new EssentialsConfigScreen(parent);
    }

    private void buildCategories() {
        categories.clear();

        // ========== VISUALS ==========
        categories.put("— VISUALS —", List.of(new SectionHeader("Visual Settings")));

        // Emissive Textures
        List<SettingEntry> emissive = new ArrayList<>();
        emissive.add(tip(new BoolEntry("Enable Emissive Overlays", () -> EssentialsConfig.emissiveEnabled, v -> EssentialsConfig.emissiveEnabled = v), "Renders glowing textures at full brightness using _e.png overlays"));
        emissive.add(child(() -> EssentialsConfig.emissiveEnabled, tip(new BoolEntry("Block Emissives", () -> EssentialsConfig.emissiveBlocks, v -> EssentialsConfig.emissiveBlocks = v), "Emissive overlays on blocks (e.g. glowing ores)")));
        emissive.add(child(() -> EssentialsConfig.emissiveEnabled, tip(new BoolEntry("Entity Emissives", () -> EssentialsConfig.emissiveEntities, v -> EssentialsConfig.emissiveEntities = v), "Emissive overlays on entities (e.g. glowing eyes)")));
        categories.put("Emissive Textures", emissive);

        // Fullbright
        List<SettingEntry> fullbright = new ArrayList<>();
        fullbright.add(tip(new BoolEntry("Enable Fullbright", () -> EssentialsConfig.fullbrightEnabled, v -> EssentialsConfig.fullbrightEnabled = v), "Toggle max brightness with a keybind (default: H)"));
        fullbright.add(child(() -> EssentialsConfig.fullbrightEnabled, tip(new BoolEntry("On By Default", () -> EssentialsConfig.fullbrightActive, v -> EssentialsConfig.fullbrightActive = v), "Start with fullbright active when the game launches")));
        categories.put("Fullbright", fullbright);

        // Fog Control
        List<SettingEntry> fog = new ArrayList<>();
        fog.add(tip(new BoolEntry("Enable Fog Control", () -> EssentialsConfig.fogEnabled, v -> EssentialsConfig.fogEnabled = v), "Control fog rendering for better visibility"));
        fog.add(new SectionHeader("Fog Types"));
        fog.add(child(() -> EssentialsConfig.fogEnabled, tip(new BoolEntry("Disable Void Fog", () -> EssentialsConfig.fogDisableVoid, v -> EssentialsConfig.fogDisableVoid = v), "Remove the dark fog below Y=0")));
        fog.add(child(() -> EssentialsConfig.fogEnabled, tip(new BoolEntry("Disable Water Fog", () -> EssentialsConfig.fogDisableWater, v -> EssentialsConfig.fogDisableWater = v), "See clearly underwater")));
        fog.add(child(() -> EssentialsConfig.fogEnabled, tip(new BoolEntry("Disable Lava Fog", () -> EssentialsConfig.fogDisableLava, v -> EssentialsConfig.fogDisableLava = v), "See through lava (fire resistance still needed)")));
        fog.add(new SectionHeader("Distance"));
        fog.add(child(() -> EssentialsConfig.fogEnabled, tip(new BoolEntry("Disable Distance Fog", () -> EssentialsConfig.fogDisableDistance, v -> EssentialsConfig.fogDisableDistance = v), "Remove distance-based fog at render distance edge")));
        fog.add(child(() -> EssentialsConfig.fogEnabled, tip(new FloatEntry("Distance Multiplier", () -> EssentialsConfig.fogDistanceMultiplier, v -> EssentialsConfig.fogDistanceMultiplier = v, 0.25f, 0.0f, 2.0f), "Scale fog distance (0 = no fog, 2 = double distance)")));
        categories.put("Fog Control", fog);

        // ========== HUD ==========
        categories.put("— HUD —", List.of(new SectionHeader("HUD Settings")));

        // HUD Overlays
        List<SettingEntry> hudOverlays = new ArrayList<>();
        hudOverlays.add(new SectionHeader("Death Waypoint"));
        hudOverlays.add(tip(new BoolEntry("Enable Death Waypoint", () -> EssentialsConfig.deathWaypointEnabled, v -> EssentialsConfig.deathWaypointEnabled = v), "Show death coordinates in chat when you die"));
        hudOverlays.add(new SectionHeader("Coordinates HUD"));
        hudOverlays.add(tip(new BoolEntry("Enable Coords HUD", () -> EssentialsConfig.coordsHudEnabled, v -> EssentialsConfig.coordsHudEnabled = v), "Display XYZ coordinates and facing direction on screen"));
        hudOverlays.add(child(() -> EssentialsConfig.coordsHudEnabled,
                tip(new SliderEntry("Coords Size", 25, 100,
                        () -> Math.round(EssentialsConfig.coordsHudScale * 100),
                        v -> EssentialsConfig.coordsHudScale = v / 100.0f,
                        v -> v + "%"), "Scale of the coordinates overlay")));
        hudOverlays.add(new SectionHeader("Armor Durability"));
        hudOverlays.add(tip(new BoolEntry("Enable Durability Bars", () -> EssentialsConfig.durabilityHudEnabled, v -> EssentialsConfig.durabilityHudEnabled = v), "Show colored durability bars for equipped armor"));
        hudOverlays.add(child(() -> EssentialsConfig.durabilityHudEnabled,
                tip(new SliderEntry("Durability Size", 25, 100,
                        () -> Math.round(EssentialsConfig.durabilityHudScale * 100),
                        v -> EssentialsConfig.durabilityHudScale = v / 100.0f,
                        v -> v + "%"), "Scale of the durability bars overlay")));
        hudOverlays.add(new SectionHeader("Pickup Notifier"));
        hudOverlays.add(tip(new BoolEntry("Enable Pickup Notifier", () -> EssentialsConfig.pickupNotifierEnabled, v -> EssentialsConfig.pickupNotifierEnabled = v), "Show item icon + count when you pick up items"));
        hudOverlays.add(child(() -> EssentialsConfig.pickupNotifierEnabled,
                tip(new SliderEntry("Pickup Size", 25, 100,
                        () -> Math.round(EssentialsConfig.pickupNotifierScale * 100),
                        v -> EssentialsConfig.pickupNotifierScale = v / 100.0f,
                        v -> v + "%"), "Scale of the pickup notification")));
        categories.put("HUD Overlays", hudOverlays);

        // Health Bars
        List<SettingEntry> healthBars = new ArrayList<>();
        healthBars.add(tip(new BoolEntry("Enable Health Bars", () -> EssentialsConfig.healthBarsEnabled, v -> EssentialsConfig.healthBarsEnabled = v), "Show colored health bars above living entities"));
        healthBars.add(new SectionHeader("Mob Health Bars"));
        healthBars.add(child(() -> EssentialsConfig.healthBarsEnabled, tip(new BoolEntry("Enable Mob Health Bars", () -> EssentialsConfig.healthBarsMobs, v -> EssentialsConfig.healthBarsMobs = v), "Show health bars on hostile and passive mobs")));
        healthBars.add(child(() -> EssentialsConfig.healthBarsEnabled && EssentialsConfig.healthBarsMobs, tip(new BoolEntry("Show Mob Health At Full", () -> EssentialsConfig.healthBarsMobsFull, v -> EssentialsConfig.healthBarsMobsFull = v), "Show health bar even when mob is at full health")));
        healthBars.add(new SectionHeader("Player Health Bars"));
        healthBars.add(child(() -> EssentialsConfig.healthBarsEnabled, tip(new BoolEntry("Enable Player Health Bars", () -> EssentialsConfig.healthBarsPlayers, v -> EssentialsConfig.healthBarsPlayers = v), "Show health bars above other players")));
        healthBars.add(child(() -> EssentialsConfig.healthBarsEnabled && EssentialsConfig.healthBarsPlayers, tip(new BoolEntry("Show Player Health At Full", () -> EssentialsConfig.healthBarsPlayersFull, v -> EssentialsConfig.healthBarsPlayersFull = v), "Show health bar even when player is at full health")));
        categories.put("Health Bars", healthBars);

        // Effect Timer
        List<SettingEntry> statusEffect = new ArrayList<>();
        statusEffect.add(tip(new BoolEntry("Enable Effect Timer", () -> EssentialsConfig.statusEffectTimerEnabled, v -> EssentialsConfig.statusEffectTimerEnabled = v), "Show duration progress bars on active status effect icons"));
        categories.put("Effect Timer", statusEffect);

        // ========== GAMEPLAY ==========
        categories.put("— GAMEPLAY —", List.of(new SectionHeader("Gameplay Settings")));

        // Zoom
        List<SettingEntry> zoom = new ArrayList<>();
        zoom.add(tip(new BoolEntry("Enable Zoom", () -> EssentialsConfig.zoomEnabled, v -> EssentialsConfig.zoomEnabled = v), "Hold a key to zoom in (default: C)"));
        zoom.add(new SectionHeader("Zoom Behavior"));
        zoom.add(child(() -> EssentialsConfig.zoomEnabled, tip(new FloatEntry("Default Zoom FOV", () -> EssentialsConfig.zoomDefaultFov, v -> EssentialsConfig.zoomDefaultFov = v, 5.0f, 5.0f, 70.0f), "Field of view when zoom is activated")));
        zoom.add(child(() -> EssentialsConfig.zoomEnabled, tip(new FloatEntry("Scroll Sensitivity", () -> EssentialsConfig.zoomScrollSensitivity, v -> EssentialsConfig.zoomScrollSensitivity = v, 1.0f, 1.0f, 10.0f), "How much scroll wheel adjusts zoom level")));
        zoom.add(child(() -> EssentialsConfig.zoomEnabled, tip(new FloatEntry("Transition Speed", () -> EssentialsConfig.zoomTransitionSpeed, v -> EssentialsConfig.zoomTransitionSpeed = v, 0.05f, 0.05f, 0.5f), "How fast the zoom animates in and out")));
        zoom.add(new SectionHeader("Sensitivity"));
        zoom.add(child(() -> EssentialsConfig.zoomEnabled, tip(new BoolEntry("Reduce Mouse Sensitivity", () -> EssentialsConfig.zoomReduceSensitivity, v -> EssentialsConfig.zoomReduceSensitivity = v), "Lower mouse sensitivity while zoomed for precision")));
        zoom.add(child(() -> EssentialsConfig.zoomEnabled, tip(new BoolEntry("Smooth Camera While Zooming", () -> EssentialsConfig.zoomSmoothCamera, v -> EssentialsConfig.zoomSmoothCamera = v), "Enable cinematic camera smoothing while zoomed")));
        categories.put("Zoom", zoom);

        // Auto Click/Hold
        List<SettingEntry> autoClick = new ArrayList<>();
        autoClick.add(tip(new BoolEntry("Enable Auto Click/Hold", () -> EssentialsConfig.autoClickEnabled, v -> EssentialsConfig.autoClickEnabled = v), "Auto-click or hold mouse button with a keybind (default: `)"));
        autoClick.add(new SectionHeader("Button Config"));
        autoClick.add(child(() -> EssentialsConfig.autoClickEnabled, new SettingEntry("Button") {
            @Override String getDisplayValue() { return EssentialsConfig.autoClickButton == 0 ? "Left Click" : "Right Click"; }
            @Override void onClick() { EssentialsConfig.autoClickButton = EssentialsConfig.autoClickButton == 0 ? 1 : 0; }
            @Override int getValueColor() { return EssentialsConfig.autoClickButton == 0 ? 0xFFFF8888 : 0xFF88FF88; }
        }));
        autoClick.add(new SectionHeader("Speed Config"));
        autoClick.add(child(() -> EssentialsConfig.autoClickEnabled, new SettingEntry("Mode") {
            @Override String getDisplayValue() { return EssentialsConfig.autoClickMode == 0 ? "Hold" : "Click"; }
            @Override void onClick() { EssentialsConfig.autoClickMode = EssentialsConfig.autoClickMode == 0 ? 1 : 0; }
            @Override int getValueColor() { return EssentialsConfig.autoClickMode == 0 ? 0xFF88CCFF : 0xFFFFCC88; }
        }));
        autoClick.add(new ChildEntry("Click Speed (CPS)", () -> EssentialsConfig.autoClickEnabled && EssentialsConfig.autoClickMode == 1,
                new SliderEntry("Click Speed", 1, 20,
                        () -> EssentialsConfig.autoClickCPS,
                        v -> EssentialsConfig.autoClickCPS = v,
                        v -> v + " CPS")));
        categories.put("Auto Click/Hold", autoClick);

        // ========== INVENTORY ==========
        categories.put("— INVENTORY —", List.of(new SectionHeader("Inventory Management")));

        List<SettingEntry> inventory = new ArrayList<>();
        inventory.add(tip(new BoolEntry("Hotbar Auto-Refill", () -> EssentialsConfig.hotbarRefillEnabled, v -> EssentialsConfig.hotbarRefillEnabled = v), "Automatically replace used-up hotbar items from inventory"));
        inventory.add(child(() -> EssentialsConfig.hotbarRefillEnabled, tip(new BoolEntry("Refill on Drop", () -> EssentialsConfig.refillOnDrop, v -> EssentialsConfig.refillOnDrop = v), "Refill hotbar when dropping last item (Q key)")));
        inventory.add(child(() -> EssentialsConfig.hotbarRefillEnabled, tip(new BoolEntry("Match Type Only", () -> EssentialsConfig.refillMatchTypeOnly, v -> EssentialsConfig.refillMatchTypeOnly = v), "ON: any matching item (e.g. any pickaxe). OFF: exact same item only")));
        inventory.add(new SectionHeader("Refill Categories"));
        inventory.add(child(() -> EssentialsConfig.hotbarRefillEnabled, tip(new BoolEntry("Blocks", () -> EssentialsConfig.refillBlocks, v -> EssentialsConfig.refillBlocks = v), "Cobblestone, Planks, Dirt, Glass...")));
        inventory.add(child(() -> EssentialsConfig.hotbarRefillEnabled, tip(new BoolEntry("Tools", () -> EssentialsConfig.refillTools, v -> EssentialsConfig.refillTools = v), "Pickaxe, Shovel, Axe, Hoe, Shears")));
        inventory.add(child(() -> EssentialsConfig.hotbarRefillEnabled, tip(new BoolEntry("Weapons", () -> EssentialsConfig.refillWeapons, v -> EssentialsConfig.refillWeapons = v), "Sword, Mace, Trident")));
        inventory.add(child(() -> EssentialsConfig.hotbarRefillEnabled, tip(new BoolEntry("Food", () -> EssentialsConfig.refillFood, v -> EssentialsConfig.refillFood = v), "Steak, Bread, Golden Carrots...")));
        inventory.add(child(() -> EssentialsConfig.hotbarRefillEnabled, tip(new BoolEntry("Containers", () -> EssentialsConfig.refillContainers, v -> EssentialsConfig.refillContainers = v), "Shulker Boxes, Bundles")));
        inventory.add(child(() -> EssentialsConfig.hotbarRefillEnabled, tip(new BoolEntry("Potions", () -> EssentialsConfig.refillPotions, v -> EssentialsConfig.refillPotions = v), "Potions, Splash Potions, Lingering")));
        inventory.add(child(() -> EssentialsConfig.hotbarRefillEnabled, tip(new BoolEntry("Projectiles", () -> EssentialsConfig.refillProjectiles, v -> EssentialsConfig.refillProjectiles = v), "Arrows, Snowballs, Eggs, Ender Pearls")));
        inventory.add(child(() -> EssentialsConfig.hotbarRefillEnabled, tip(new BoolEntry("Other", () -> EssentialsConfig.refillOther, v -> EssentialsConfig.refillOther = v), "Torches, Buckets, Rockets, Leads...")));
        inventory.add(new SectionHeader("Sorting"));
        inventory.add(tip(new BoolEntry("Inventory Sort Button", () -> EssentialsConfig.inventorySortEnabled, v -> EssentialsConfig.inventorySortEnabled = v), "Show sort/move/match buttons in inventory and container screens"));
        categories.put("Inventory", inventory);

        // ========== TOOLTIPS ==========
        categories.put("— TOOLTIPS —", List.of(new SectionHeader("Tooltip Settings")));

        List<SettingEntry> tooltips = new ArrayList<>();
        tooltips.add(new SectionHeader("Container Previews"));
        tooltips.add(tip(new BoolEntry("Shulker Box Preview", () -> EssentialsConfig.shulkerTooltipEnabled, v -> EssentialsConfig.shulkerTooltipEnabled = v), "Show visual 9x3 item grid when hovering shulker boxes"));
        tooltips.add(tip(new BoolEntry("Ender Chest Preview", () -> EssentialsConfig.enderChestTooltipEnabled, v -> EssentialsConfig.enderChestTooltipEnabled = v), "Show ender chest contents (cached after first open per world)"));
        tooltips.add(tip(new BoolEntry("Map Preview", () -> EssentialsConfig.mapTooltipEnabled, v -> EssentialsConfig.mapTooltipEnabled = v), "Show filled map pixel preview when hovering"));
        tooltips.add(new SectionHeader("Enhanced Info"));
        tooltips.add(tip(new BoolEntry("Enhanced Item Tooltips", () -> EssentialsConfig.enhancedTooltipsEnabled, v -> EssentialsConfig.enhancedTooltipsEnabled = v), "Add mining speed, DPS, food stats, and durability to tooltips"));
        tooltips.add(child(() -> EssentialsConfig.enhancedTooltipsEnabled, tip(new BoolEntry("Tool Stats", () -> EssentialsConfig.showToolStats, v -> EssentialsConfig.showToolStats = v), "Show mining speed and durability for tools")));
        tooltips.add(child(() -> EssentialsConfig.enhancedTooltipsEnabled, tip(new BoolEntry("Weapon Stats", () -> EssentialsConfig.showWeaponStats, v -> EssentialsConfig.showWeaponStats = v), "Show attack damage, speed, and DPS for weapons")));
        tooltips.add(child(() -> EssentialsConfig.enhancedTooltipsEnabled, tip(new BoolEntry("Food Stats", () -> EssentialsConfig.showFoodStats, v -> EssentialsConfig.showFoodStats = v), "Show hunger points, saturation, and quality rating for food")));
        tooltips.add(new SectionHeader("Per-Item Stats"));
        tooltips.add(child(() -> EssentialsConfig.enhancedTooltipsEnabled, tip(statModeEntry("Pickaxe", () -> EssentialsConfig.pickaxeStatMode, v -> EssentialsConfig.pickaxeStatMode = v), "Click to cycle: Tool / Weapon / Both")));
        tooltips.add(child(() -> EssentialsConfig.enhancedTooltipsEnabled, tip(statModeEntry("Axe", () -> EssentialsConfig.axeStatMode, v -> EssentialsConfig.axeStatMode = v), "Click to cycle: Tool / Weapon / Both")));
        tooltips.add(child(() -> EssentialsConfig.enhancedTooltipsEnabled, tip(statModeEntry("Shovel", () -> EssentialsConfig.shovelStatMode, v -> EssentialsConfig.shovelStatMode = v), "Click to cycle: Tool / Weapon / Both")));
        tooltips.add(child(() -> EssentialsConfig.enhancedTooltipsEnabled, tip(statModeEntry("Hoe", () -> EssentialsConfig.hoeStatMode, v -> EssentialsConfig.hoeStatMode = v), "Click to cycle: Tool / Weapon / Both")));
        tooltips.add(child(() -> EssentialsConfig.enhancedTooltipsEnabled, tip(statModeEntry("Sword", () -> EssentialsConfig.swordStatMode, v -> EssentialsConfig.swordStatMode = v), "Click to cycle: Tool / Weapon / Both")));
        tooltips.add(child(() -> EssentialsConfig.enhancedTooltipsEnabled, tip(statModeEntry("Mace", () -> EssentialsConfig.maceStatMode, v -> EssentialsConfig.maceStatMode = v), "Click to cycle: Tool / Weapon / Both")));
        categories.put("Tooltips", tooltips);

        // ========== PERFORMANCE ==========
        categories.put("— PERFORMANCE —", List.of(new SectionHeader("Performance Settings")));

        // Dynamic FPS
        List<SettingEntry> dfps = new ArrayList<>();
        dfps.add(tip(new BoolEntry("Enable Dynamic FPS", () -> EssentialsConfig.dynamicFpsEnabled, v -> EssentialsConfig.dynamicFpsEnabled = v), "Reduce FPS when game window is unfocused or minimized"));
        dfps.add(child(() -> EssentialsConfig.dynamicFpsEnabled, tip(new IntEntry("Unfocused FPS Cap", () -> EssentialsConfig.dynamicFpsUnfocusedCap, v -> EssentialsConfig.dynamicFpsUnfocusedCap = v), "Max FPS when the game window is not focused")));
        dfps.add(child(() -> EssentialsConfig.dynamicFpsEnabled, tip(new IntEntry("Minimized FPS Cap", () -> EssentialsConfig.dynamicFpsMinimizedCap, v -> EssentialsConfig.dynamicFpsMinimizedCap = v), "Max FPS when the game is minimized to taskbar")));
        categories.put("Dynamic FPS", dfps);

        // Borderless
        List<SettingEntry> borderless = new ArrayList<>();
        borderless.add(tip(new BoolEntry("Enable Borderless Mode", () -> EssentialsConfig.borderlessEnabled, v -> EssentialsConfig.borderlessEnabled = v), "Use borderless fullscreen for faster alt-tab"));
        categories.put("Borderless", borderless);
    }

    private static SettingEntry statModeEntry(String name, Supplier<Integer> getter, Consumer<Integer> setter) {
        return new SettingEntry(name) {
            @Override String getDisplayValue() {
                return switch (getter.get()) {
                    case 0 -> "Tool";
                    case 1 -> "Weapon";
                    case 2 -> "Both";
                    default -> "Tool";
                };
            }
            @Override void onClick() { setter.accept((getter.get() + 1) % 3); }
            @Override int getValueColor() {
                return switch (getter.get()) {
                    case 0 -> 0xFF55FFFF; // Aqua for tool
                    case 1 -> 0xFFFF5555; // Red for weapon
                    case 2 -> 0xFFFFFF55; // Yellow for both
                    default -> 0xFFFFFFFF;
                };
            }
        };
    }

    private static ChildEntry child(Supplier<Boolean> parent, SettingEntry delegate) {
        return new ChildEntry(delegate.name, parent, delegate);
    }

    private static <T extends SettingEntry> T tip(T entry, String tooltip) {
        entry.tooltip = tooltip;
        return entry;
    }

    @Override
    protected void init() {
        int clearBtnW = 20;
        searchBox = new EditBox(this.font, PADDING, PADDING, this.width - PADDING * 2 - clearBtnW - 2, 18, Component.literal("Search..."));
        searchBox.setHint(Component.literal("Search all settings...").withStyle(ChatFormatting.GRAY));
        searchBox.setResponder(query -> {
            this.searchQuery = query.toLowerCase();
            rebuildSettingsPanel();
        });
        addRenderableWidget(searchBox);

        // Clear button
        addRenderableWidget(Button.builder(Component.literal("X"), btn -> {
            searchBox.setValue("");
            searchBox.setFocused(false);
        }).bounds(this.width - PADDING - clearBtnW, PADDING, clearBtnW, 18).build());

        int listTop = PADDING + 22;
        int listBottom = this.height - 30;
        categoryList = new CategoryList(this.minecraft, SIDEBAR_WIDTH, listBottom - listTop, listTop, 20);
        categoryList.setX(PADDING);
        for (String cat : categories.keySet()) {
            categoryList.addCategoryEntry(cat);
        }
        addRenderableWidget(categoryList);

        int panelLeft = SIDEBAR_WIDTH + PADDING * 2;
        int panelWidth = this.width - panelLeft - PADDING;
        settingsPanel = new SettingsPanel(this.minecraft, panelWidth, listBottom - listTop, listTop, 22);
        settingsPanel.setX(panelLeft);
        rebuildSettingsPanel();
        addRenderableWidget(settingsPanel);

        addRenderableWidget(Button.builder(Component.literal("Reset Defaults"), btn -> {
            resetAllDefaults();
            buildCategories();
            rebuildSettingsPanel();
        }).bounds(this.width / 2 - 205, this.height - 26, 100, 20).build());

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, btn -> {
            EssentialsConfig.save();
            this.minecraft.setScreen(this.parent);
        }).bounds(this.width / 2 - 100, this.height - 26, 200, 20).build());
    }

    private void rebuildSettingsPanel() {
        if (settingsPanel == null) return;
        settingsPanel.clear();
        settingsPanel.setScrollAmount(0);

        if (!searchQuery.isEmpty()) {
            // Global search — show matching settings from ALL categories
            for (var catEntry : categories.entrySet()) {
                // Skip group headers in search results
                if (catEntry.getKey().startsWith("—")) continue;
                boolean headerAdded = false;
                for (SettingEntry entry : catEntry.getValue()) {
                    if (entry instanceof SectionHeader) continue; // Skip section headers in search
                    if (entry.name.toLowerCase().contains(searchQuery)) {
                        if (!headerAdded) {
                            settingsPanel.addSettingEntry(new SectionHeader(catEntry.getKey()));
                            headerAdded = true;
                        }
                        settingsPanel.addSettingEntry(entry);
                    }
                }
            }
        } else {
            // Normal — show selected category
            List<SettingEntry> entries = categories.getOrDefault(selectedCategory, List.of());
            for (SettingEntry entry : entries) {
                settingsPanel.addSettingEntry(entry);
            }
        }
    }

    @Override
    public void extractRenderState(net.minecraft.client.gui.GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        // Clear pending tooltip each frame
        pendingTooltip = null;

        // Render all widgets (this populates pendingTooltip if a setting is hovered 2s+)
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        // Draw tooltip on top of everything, MC-style
        if (pendingTooltip != null) {
            int tipW = font.width(pendingTooltip);
            int padding = 4;
            int boxW = tipW + padding * 2;
            int boxH = 10 + padding * 2;

            // Default: top-left of tooltip box just below the mouse pointer
            int tipX = pendingTooltipX;
            int tipY = pendingTooltipY + 12;

            // If tooltip overflows right edge, flip so top-right is at mouse
            if (tipX + boxW > this.width) {
                tipX = pendingTooltipX - boxW;
            }
            // If it overflows bottom, push up above mouse
            if (tipY + boxH > this.height) {
                tipY = pendingTooltipY - boxH - 2;
            }

            int x1 = tipX;
            int y1 = tipY;
            int x2 = tipX + boxW;
            int y2 = tipY + boxH;

            // MC-style tooltip: dark background with purple border
            int bgColor = 0xF0100010;
            int borderTop = 0xFF5000FF;
            int borderBot = 0xFF28007F;

            // Background
            graphics.fill(x1 + 1, y1, x2 - 1, y1 + 1, bgColor);
            graphics.fill(x1 + 1, y2 - 1, x2 - 1, y2, bgColor);
            graphics.fill(x1, y1 + 1, x2, y2 - 1, bgColor);
            // Border
            graphics.fill(x1 + 1, y1 + 1, x2 - 1, y1 + 2, borderTop);
            graphics.fill(x1 + 1, y2 - 2, x2 - 1, y2 - 1, borderBot);
            graphics.fill(x1, y1 + 2, x1 + 1, y2 - 2, borderTop);
            graphics.fill(x2 - 1, y1 + 2, x2, y2 - 2, borderBot);

            graphics.text(font, pendingTooltip, x1 + padding, y1 + padding, 0xFFFFFFFF);
        }
    }

    public void selectCategory(String category) {
        this.selectedCategory = category;
        rebuildSettingsPanel();
    }

    private void resetAllDefaults() {
        EssentialsConfig.emissiveEnabled = true;
        EssentialsConfig.emissiveBlocks = true;
        EssentialsConfig.emissiveEntities = true;
        EssentialsConfig.zoomEnabled = true;
        EssentialsConfig.zoomDefaultFov = 30.0f;
        EssentialsConfig.zoomScrollSensitivity = 5.0f;
        EssentialsConfig.zoomTransitionSpeed = 0.15f;
        EssentialsConfig.zoomReduceSensitivity = true;
        EssentialsConfig.zoomSmoothCamera = false;
        EssentialsConfig.fullbrightEnabled = true;
        EssentialsConfig.fullbrightActive = false;
        EssentialsConfig.fogEnabled = true;
        EssentialsConfig.fogDisableVoid = true;
        EssentialsConfig.fogDisableWater = true;
        EssentialsConfig.fogDisableLava = false;
        EssentialsConfig.fogDisableDistance = false;
        EssentialsConfig.fogDistanceMultiplier = 1.0f;
        EssentialsConfig.dynamicFpsEnabled = true;
        EssentialsConfig.dynamicFpsUnfocusedCap = 15;
        EssentialsConfig.dynamicFpsMinimizedCap = 1;
        EssentialsConfig.borderlessEnabled = true;
        EssentialsConfig.healthBarsEnabled = true;
        EssentialsConfig.healthBarsMobs = true;
        EssentialsConfig.healthBarsMobsFull = false;
        EssentialsConfig.healthBarsPlayers = true;
        EssentialsConfig.healthBarsPlayersFull = false;
        EssentialsConfig.statusEffectTimerEnabled = true;
        EssentialsConfig.coordsHudEnabled = false;
        EssentialsConfig.coordsHudScale = 1.0f;
        EssentialsConfig.deathWaypointEnabled = true;
        EssentialsConfig.durabilityHudEnabled = true;
        EssentialsConfig.durabilityHudScale = 0.5f;
        EssentialsConfig.pickupNotifierEnabled = true;
        EssentialsConfig.pickupNotifierScale = 0.75f;
        EssentialsConfig.autoClickEnabled = true;
        EssentialsConfig.autoClickMode = 0;
        EssentialsConfig.autoClickButton = 0;
        EssentialsConfig.autoClickCPS = 5;
        EssentialsConfig.shulkerTooltipEnabled = true;
        EssentialsConfig.enderChestTooltipEnabled = true;
        EssentialsConfig.mapTooltipEnabled = true;
        EssentialsConfig.enhancedTooltipsEnabled = true;
        EssentialsConfig.showToolStats = true;
        EssentialsConfig.showWeaponStats = true;
        EssentialsConfig.showFoodStats = true;
        EssentialsConfig.pickaxeStatMode = 0;
        EssentialsConfig.axeStatMode = 2;
        EssentialsConfig.shovelStatMode = 0;
        EssentialsConfig.hoeStatMode = 0;
        EssentialsConfig.swordStatMode = 1;
        EssentialsConfig.maceStatMode = 1;
        EssentialsConfig.hotbarRefillEnabled = true;
        EssentialsConfig.refillOnDrop = false;
        EssentialsConfig.refillMatchTypeOnly = true;
        EssentialsConfig.refillBlocks = true;
        EssentialsConfig.refillTools = true;
        EssentialsConfig.refillWeapons = true;
        EssentialsConfig.refillFood = true;
        EssentialsConfig.refillContainers = true;
        EssentialsConfig.refillPotions = true;
        EssentialsConfig.refillProjectiles = true;
        EssentialsConfig.refillOther = true;
        EssentialsConfig.inventorySortEnabled = true;
        EssentialsConfig.save();
    }

    @Override
    public void onClose() {
        EssentialsConfig.save();
        this.minecraft.setScreen(this.parent);
    }

    // === Category List ===
    class CategoryList extends ObjectSelectionList<CategoryList.CatEntry> {

        public CategoryList(Minecraft mc, int width, int height, int y, int itemHeight) {
            super(mc, width, height, y, itemHeight);
        }

        public void addCategoryEntry(String name) {
            addEntry(new CatEntry(name));
        }

        @Override
        public int getRowWidth() {
            return this.width - 6;
        }

        class CatEntry extends ObjectSelectionList.Entry<CatEntry> {
            private final String name;

            CatEntry(String name) {
                this.name = name;
            }

            @Override
            public Component getNarration() {
                return Component.literal(name);
            }

            @Override
            public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float delta) {
                int x = getContentX();
                int y = getContentY();
                int w = getContentWidth();
                int h = getContentHeight();

                // Group headers (e.g. "— VISUALS —") are non-clickable dividers
                if (name.startsWith("—")) {
                    graphics.fill(x, y + h / 2, x + w, y + h / 2 + 1, 0xFF555555);
                    String label = name.replace("—", "").trim();
                    int labelW = font.width(label);
                    int labelX = x + (w - labelW) / 2;
                    graphics.fill(labelX - 4, y + 2, labelX + labelW + 4, y + h - 2, 0xFF222222);
                    graphics.text(font, label, labelX, y + (h - 9) / 2, 0xFF888888);
                    return;
                }

                boolean selected = name.equals(selectedCategory);
                int color = selected ? 0xFFFFFFFF : (hovered ? 0xFFDDDDDD : 0xFFAAAAAA);
                if (selected) {
                    graphics.fill(x, y, x + w, y + h, 0x40FFFFFF);
                }
                graphics.text(font, name, x + 6, y + (h - 9) / 2, color);
            }

            @Override
            public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean direct) {
                if (name.startsWith("—")) return false; // Group headers not clickable
                selectCategory(name);
                return true;
            }
        }
    }

    // === Settings Panel ===
    class SettingsPanel extends ObjectSelectionList<SettingsPanel.SettEntry> {

        public SettingsPanel(Minecraft mc, int width, int height, int y, int itemHeight) {
            super(mc, width, height, y, itemHeight);
        }

        public void addSettingEntry(SettingEntry setting) {
            addEntry(new SettEntry(setting));
        }

        public void clear() {
            clearEntries();
        }

        @Override
        public int getRowWidth() {
            return this.width - 16;
        }

        class SettEntry extends ObjectSelectionList.Entry<SettEntry> {
            private final SettingEntry setting;

            SettEntry(SettingEntry setting) {
                this.setting = setting;
            }

            @Override
            public Component getNarration() {
                return Component.literal(setting.name);
            }

            @Override
            public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float delta) {
                int x = getContentX();
                int y = getContentY();
                int w = getContentWidth();
                int h = getContentHeight();

                // Section header — draw separator line and bold label, skip value
                if (setting instanceof SectionHeader) {
                    graphics.fill(x, y, x + w, y + 1, 0xFF666666);
                    graphics.fill(x, y + 1, x + w, y + h, 0x30FFFFFF);
                    graphics.text(font, setting.name, x + 4, y + (h - 9) / 2, 0xFFFFCC00, true);
                    return;
                }

                // Hover highlight on entire row
                if (hovered) {
                    graphics.fill(x, y, x + w, y + h, 0x20FFFFFF);
                }

                // Label on left
                graphics.text(font, setting.name, x + 4, y + (h - 9) / 2, setting.getNameColor());

                // Value area on right
                int btnW = 70;
                int btnX = x + w - btnW;
                int btnY = y + 1;
                int btnH = h - 2;

                // Check if this is a slider (unwrap ChildEntry if needed)
                SettingEntry actual = setting;
                if (actual instanceof ChildEntry ce) actual = ce.delegate;

                if (actual instanceof SliderEntry slider) {
                    // MC-style slider
                    int sliderW = 100;
                    int sliderX = x + w - sliderW;
                    int sliderH = h - 4;
                    int sliderY = y + 2;

                    boolean hoverSlider = mouseX >= sliderX && mouseX <= sliderX + sliderW
                            && mouseY >= sliderY && mouseY <= sliderY + sliderH;

                    // Handle dragging: if mouse is held and was on slider, update value
                    if (hoverSlider && org.lwjgl.glfw.GLFW.glfwGetMouseButton(
                            Minecraft.getInstance().getWindow().handle(),
                            org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS) {
                        double progress = (double)(mouseX - sliderX) / sliderW;
                        progress = Math.clamp(progress, 0.0, 1.0);
                        slider.setFromProgress(progress);
                    }

                    double progress = slider.getProgress();

                    // Track background
                    graphics.fill(sliderX, sliderY, sliderX + sliderW, sliderY + sliderH,
                            hoverSlider ? 0xFF555555 : 0xFF333333);
                    // Filled portion
                    int fillW = (int)(sliderW * progress);
                    graphics.fill(sliderX, sliderY, sliderX + fillW, sliderY + sliderH, 0xFF66BB66);
                    // Handle (4px wide)
                    int handleX = sliderX + fillW - 2;
                    graphics.fill(Math.max(sliderX, handleX), sliderY, Math.min(sliderX + sliderW, handleX + 4), sliderY + sliderH, 0xFFFFFFFF);

                    // Value text centered on slider
                    String valueStr = slider.getDisplayValue();
                    int textW = font.width(valueStr);
                    graphics.text(font, valueStr, sliderX + (sliderW - textW) / 2, sliderY + (sliderH - 9) / 2, 0xFFFFFFFF);
                } else {
                    // Normal click button
                    boolean hoverBtn = mouseX >= btnX && mouseX <= btnX + btnW
                            && mouseY >= btnY && mouseY <= btnY + btnH;
                    int bgColor = hoverBtn ? 0x90808080 : 0x60606060;
                    graphics.fill(btnX, btnY, btnX + btnW, btnY + btnH, bgColor);

                    String valueStr = setting.getDisplayValue();
                    int textColor = setting.getValueColor();
                    int textW = font.width(valueStr);
                    graphics.text(font, valueStr, btnX + (btnW - textW) / 2, btnY + (btnH - 9) / 2, textColor);
                }

                // Set tooltip for screen-level rendering (on top of everything)
                if (hovered && setting.getTooltip() != null) {
                    pendingTooltip = setting.getTooltip();
                    pendingTooltipX = mouseX;
                    pendingTooltipY = mouseY;
                }
            }

            @Override
            public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean direct) {
                // Slider handles its own input via GLFW polling in extractContent
                setting.onClick();
                return true;
            }
        }
    }

    // === Setting Types ===
    abstract static class SettingEntry {
        final String name;
        String tooltip;
        SettingEntry(String name) { this.name = name; }
        abstract String getDisplayValue();
        abstract void onClick();
        int getValueColor() { return 0xFFFFFFFF; }
        int getNameColor() { return 0xFFFFFFFF; }
        boolean isEnabled() { return true; }
        String getTooltip() { return tooltip; }
        SettingEntry withTooltip(String tip) { this.tooltip = tip; return this; }
    }

    static class ChildEntry extends SettingEntry {
        final Supplier<Boolean> parentEnabled;
        final SettingEntry delegate;
        ChildEntry(String name, Supplier<Boolean> parentEnabled, SettingEntry delegate) {
            super(name);
            this.parentEnabled = parentEnabled;
            this.delegate = delegate;
        }
        @Override boolean isEnabled() { return parentEnabled.get(); }
        @Override String getDisplayValue() { return delegate.getDisplayValue(); }
        @Override void onClick() { if (isEnabled()) delegate.onClick(); }
        @Override int getValueColor() { return isEnabled() ? delegate.getValueColor() : 0xFF666666; }
        @Override int getNameColor() { return isEnabled() ? 0xFFFFFFFF : 0xFF666666; }
        @Override String getTooltip() { return delegate.getTooltip() != null ? delegate.getTooltip() : tooltip; }
    }

    static class BoolEntry extends SettingEntry {
        final Supplier<Boolean> getter;
        final Consumer<Boolean> setter;
        BoolEntry(String name, Supplier<Boolean> getter, Consumer<Boolean> setter) {
            super(name); this.getter = getter; this.setter = setter;
        }
        @Override String getDisplayValue() { return getter.get() ? "ON" : "OFF"; }
        @Override void onClick() { setter.accept(!getter.get()); }
        @Override int getValueColor() { return getter.get() ? 0xFF55FF55 : 0xFFFF5555; }
    }

    static class FloatEntry extends SettingEntry {
        final Supplier<Float> getter;
        final Consumer<Float> setter;
        final float step;
        final float min;
        final float max;
        FloatEntry(String name, Supplier<Float> getter, Consumer<Float> setter) {
            this(name, getter, setter, 5.0f, 0.0f, 100.0f);
        }
        FloatEntry(String name, Supplier<Float> getter, Consumer<Float> setter, float step, float min, float max) {
            super(name); this.getter = getter; this.setter = setter;
            this.step = step; this.min = min; this.max = max;
        }
        @Override String getDisplayValue() { return String.format("%.1f", getter.get()); }
        @Override void onClick() {
            float v = getter.get() + step;
            if (v > max) v = min;
            setter.accept(v);
        }
    }

    static class PercentEntry extends SettingEntry {
        final Supplier<Float> getter;
        final Consumer<Float> setter;
        PercentEntry(String name, Supplier<Float> getter, Consumer<Float> setter) {
            super(name); this.getter = getter; this.setter = setter;
        }
        @Override String getDisplayValue() { return Math.round(getter.get() * 100) + "%"; }
        @Override void onClick() {
            float v = getter.get();
            if (v <= 0.2f) setter.accept(0.3f);
            else if (v <= 0.3f) setter.accept(0.4f);
            else if (v <= 0.4f) setter.accept(0.5f);
            else if (v <= 0.5f) setter.accept(0.6f);
            else if (v <= 0.6f) setter.accept(0.8f);
            else if (v <= 0.8f) setter.accept(1.0f);
            else setter.accept(0.2f);
        }
    }

    static class LevelEntry extends SettingEntry {
        final Supplier<Integer> getter;
        final Consumer<Integer> setter;
        LevelEntry(String name, Supplier<Integer> getter, Consumer<Integer> setter) {
            super(name); this.getter = getter; this.setter = setter;
        }
        @Override String getDisplayValue() { return "Level " + getter.get(); }
        @Override void onClick() {
            int v = getter.get();
            setter.accept(v >= 5 ? 1 : v + 1);
        }
    }

    static class IntEntry extends SettingEntry {
        final Supplier<Integer> getter;
        final Consumer<Integer> setter;
        IntEntry(String name, Supplier<Integer> getter, Consumer<Integer> setter) {
            super(name); this.getter = getter; this.setter = setter;
        }
        @Override String getDisplayValue() { return String.valueOf(getter.get()); }
        @Override void onClick() {
            int v = getter.get();
            if (v <= 1) setter.accept(5);
            else if (v <= 5) setter.accept(15);
            else if (v <= 15) setter.accept(30);
            else if (v <= 30) setter.accept(60);
            else setter.accept(1);
        }
    }

    static class SliderEntry extends SettingEntry {
        final int min, max;
        final Supplier<Integer> getter;
        final Consumer<Integer> setter;
        final java.util.function.IntFunction<String> formatter;
        SliderEntry(String name, int min, int max, Supplier<Integer> getter, Consumer<Integer> setter, java.util.function.IntFunction<String> formatter) {
            super(name); this.min = min; this.max = max; this.getter = getter; this.setter = setter; this.formatter = formatter;
        }
        @Override String getDisplayValue() { return formatter.apply(getter.get()); }
        @Override void onClick() {
            // Clicking increments; actual sliding handled in SettEntry
            int v = getter.get() + 1;
            if (v > max) v = min;
            setter.accept(v);
        }
        double getProgress() { return (double)(getter.get() - min) / (max - min); }
        void setFromProgress(double progress) {
            int v = min + (int) Math.round(progress * (max - min));
            v = Math.clamp(v, min, max);
            setter.accept(v);
        }
    }

    static class SectionHeader extends SettingEntry {
        SectionHeader(String name) { super(name); }
        @Override String getDisplayValue() { return ""; }
        @Override void onClick() { }
        @Override int getNameColor() { return 0xFFFFCC00; }
        boolean isSectionHeader() { return true; }
    }

    static class ActionEntry extends SettingEntry {
        final Runnable action;
        ActionEntry(String name, Runnable action) {
            super(name); this.action = action;
        }
        @Override String getDisplayValue() { return ">>"; }
        @Override void onClick() { action.run(); }
        @Override int getValueColor() { return 0xFF88FF88; }
    }

}
