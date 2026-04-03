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
        emissive.add(new BoolEntry("Enable Emissive Overlays", () -> EssentialsConfig.emissiveEnabled, v -> EssentialsConfig.emissiveEnabled = v));
        emissive.add(child(() -> EssentialsConfig.emissiveEnabled, new BoolEntry("Block Emissives", () -> EssentialsConfig.emissiveBlocks, v -> EssentialsConfig.emissiveBlocks = v)));
        emissive.add(child(() -> EssentialsConfig.emissiveEnabled, new BoolEntry("Entity Emissives", () -> EssentialsConfig.emissiveEntities, v -> EssentialsConfig.emissiveEntities = v)));
        categories.put("Emissive Textures", emissive);

        // Fullbright
        List<SettingEntry> fullbright = new ArrayList<>();
        fullbright.add(new BoolEntry("Enable Fullbright", () -> EssentialsConfig.fullbrightEnabled, v -> EssentialsConfig.fullbrightEnabled = v));
        fullbright.add(child(() -> EssentialsConfig.fullbrightEnabled, new BoolEntry("On By Default", () -> EssentialsConfig.fullbrightActive, v -> EssentialsConfig.fullbrightActive = v)));
        categories.put("Fullbright", fullbright);

        // Fog Control
        List<SettingEntry> fog = new ArrayList<>();
        fog.add(new BoolEntry("Enable Fog Control", () -> EssentialsConfig.fogEnabled, v -> EssentialsConfig.fogEnabled = v));
        fog.add(new SectionHeader("Fog Types"));
        fog.add(child(() -> EssentialsConfig.fogEnabled, new BoolEntry("Disable Void Fog", () -> EssentialsConfig.fogDisableVoid, v -> EssentialsConfig.fogDisableVoid = v)));
        fog.add(child(() -> EssentialsConfig.fogEnabled, new BoolEntry("Disable Water Fog", () -> EssentialsConfig.fogDisableWater, v -> EssentialsConfig.fogDisableWater = v)));
        fog.add(child(() -> EssentialsConfig.fogEnabled, new BoolEntry("Disable Lava Fog", () -> EssentialsConfig.fogDisableLava, v -> EssentialsConfig.fogDisableLava = v)));
        fog.add(new SectionHeader("Distance"));
        fog.add(child(() -> EssentialsConfig.fogEnabled, new BoolEntry("Disable Distance Fog", () -> EssentialsConfig.fogDisableDistance, v -> EssentialsConfig.fogDisableDistance = v)));
        fog.add(child(() -> EssentialsConfig.fogEnabled, new FloatEntry("Distance Multiplier", () -> EssentialsConfig.fogDistanceMultiplier, v -> EssentialsConfig.fogDistanceMultiplier = v, 0.25f, 0.0f, 2.0f)));
        categories.put("Fog Control", fog);

        // ========== HUD ==========
        categories.put("— HUD —", List.of(new SectionHeader("HUD Settings")));

        // HUD Overlays
        List<SettingEntry> hudOverlays = new ArrayList<>();
        hudOverlays.add(new SectionHeader("Death Waypoint"));
        hudOverlays.add(new BoolEntry("Enable Death Waypoint", () -> EssentialsConfig.deathWaypointEnabled, v -> EssentialsConfig.deathWaypointEnabled = v));
        hudOverlays.add(new SectionHeader("Coordinates HUD"));
        hudOverlays.add(new BoolEntry("Enable Coords HUD", () -> EssentialsConfig.coordsHudEnabled, v -> EssentialsConfig.coordsHudEnabled = v));
        hudOverlays.add(child(() -> EssentialsConfig.coordsHudEnabled,
                new SliderEntry("Coords Size", 25, 100,
                        () -> Math.round(EssentialsConfig.coordsHudScale * 100),
                        v -> EssentialsConfig.coordsHudScale = v / 100.0f,
                        v -> v + "%")));
        hudOverlays.add(new SectionHeader("Armor Durability"));
        hudOverlays.add(new BoolEntry("Enable Durability Bars", () -> EssentialsConfig.durabilityHudEnabled, v -> EssentialsConfig.durabilityHudEnabled = v));
        hudOverlays.add(child(() -> EssentialsConfig.durabilityHudEnabled,
                new SliderEntry("Durability Size", 25, 100,
                        () -> Math.round(EssentialsConfig.durabilityHudScale * 100),
                        v -> EssentialsConfig.durabilityHudScale = v / 100.0f,
                        v -> v + "%")));
        hudOverlays.add(new SectionHeader("Pickup Notifier"));
        hudOverlays.add(new BoolEntry("Enable Pickup Notifier", () -> EssentialsConfig.pickupNotifierEnabled, v -> EssentialsConfig.pickupNotifierEnabled = v));
        hudOverlays.add(child(() -> EssentialsConfig.pickupNotifierEnabled,
                new SliderEntry("Pickup Size", 25, 100,
                        () -> Math.round(EssentialsConfig.pickupNotifierScale * 100),
                        v -> EssentialsConfig.pickupNotifierScale = v / 100.0f,
                        v -> v + "%")));
        categories.put("HUD Overlays", hudOverlays);

        // Health Bars
        List<SettingEntry> healthBars = new ArrayList<>();
        healthBars.add(new BoolEntry("Enable Health Bars", () -> EssentialsConfig.healthBarsEnabled, v -> EssentialsConfig.healthBarsEnabled = v));
        healthBars.add(new SectionHeader("Mob Health Bars"));
        healthBars.add(child(() -> EssentialsConfig.healthBarsEnabled, new BoolEntry("Enable Mob Health Bars", () -> EssentialsConfig.healthBarsMobs, v -> EssentialsConfig.healthBarsMobs = v)));
        healthBars.add(child(() -> EssentialsConfig.healthBarsEnabled && EssentialsConfig.healthBarsMobs, new BoolEntry("Show Mob Health At Full", () -> EssentialsConfig.healthBarsMobsFull, v -> EssentialsConfig.healthBarsMobsFull = v)));
        healthBars.add(new SectionHeader("Player Health Bars"));
        healthBars.add(child(() -> EssentialsConfig.healthBarsEnabled, new BoolEntry("Enable Player Health Bars", () -> EssentialsConfig.healthBarsPlayers, v -> EssentialsConfig.healthBarsPlayers = v)));
        healthBars.add(child(() -> EssentialsConfig.healthBarsEnabled && EssentialsConfig.healthBarsPlayers, new BoolEntry("Show Player Health At Full", () -> EssentialsConfig.healthBarsPlayersFull, v -> EssentialsConfig.healthBarsPlayersFull = v)));
        categories.put("Health Bars", healthBars);

        // Effect Timer
        List<SettingEntry> statusEffect = new ArrayList<>();
        statusEffect.add(new BoolEntry("Enable Effect Timer", () -> EssentialsConfig.statusEffectTimerEnabled, v -> EssentialsConfig.statusEffectTimerEnabled = v));
        categories.put("Effect Timer", statusEffect);

        // ========== GAMEPLAY ==========
        categories.put("— GAMEPLAY —", List.of(new SectionHeader("Gameplay Settings")));

        // Zoom
        List<SettingEntry> zoom = new ArrayList<>();
        zoom.add(new BoolEntry("Enable Zoom", () -> EssentialsConfig.zoomEnabled, v -> EssentialsConfig.zoomEnabled = v));
        zoom.add(new SectionHeader("Zoom Behavior"));
        zoom.add(child(() -> EssentialsConfig.zoomEnabled, new FloatEntry("Default Zoom FOV", () -> EssentialsConfig.zoomDefaultFov, v -> EssentialsConfig.zoomDefaultFov = v, 5.0f, 5.0f, 70.0f)));
        zoom.add(child(() -> EssentialsConfig.zoomEnabled, new FloatEntry("Scroll Sensitivity", () -> EssentialsConfig.zoomScrollSensitivity, v -> EssentialsConfig.zoomScrollSensitivity = v, 1.0f, 1.0f, 10.0f)));
        zoom.add(child(() -> EssentialsConfig.zoomEnabled, new FloatEntry("Transition Speed", () -> EssentialsConfig.zoomTransitionSpeed, v -> EssentialsConfig.zoomTransitionSpeed = v, 0.05f, 0.05f, 0.5f)));
        zoom.add(new SectionHeader("Sensitivity"));
        zoom.add(child(() -> EssentialsConfig.zoomEnabled, new BoolEntry("Reduce Mouse Sensitivity", () -> EssentialsConfig.zoomReduceSensitivity, v -> EssentialsConfig.zoomReduceSensitivity = v)));
        zoom.add(child(() -> EssentialsConfig.zoomEnabled, new BoolEntry("Smooth Camera While Zooming", () -> EssentialsConfig.zoomSmoothCamera, v -> EssentialsConfig.zoomSmoothCamera = v)));
        categories.put("Zoom", zoom);

        // Auto Click/Hold
        List<SettingEntry> autoClick = new ArrayList<>();
        autoClick.add(new BoolEntry("Enable Auto Click/Hold", () -> EssentialsConfig.autoClickEnabled, v -> EssentialsConfig.autoClickEnabled = v));
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

        // ========== PERFORMANCE ==========
        categories.put("— PERFORMANCE —", List.of(new SectionHeader("Performance Settings")));

        // Dynamic FPS
        List<SettingEntry> dfps = new ArrayList<>();
        dfps.add(new BoolEntry("Enable Dynamic FPS", () -> EssentialsConfig.dynamicFpsEnabled, v -> EssentialsConfig.dynamicFpsEnabled = v));
        dfps.add(child(() -> EssentialsConfig.dynamicFpsEnabled, new IntEntry("Unfocused FPS Cap", () -> EssentialsConfig.dynamicFpsUnfocusedCap, v -> EssentialsConfig.dynamicFpsUnfocusedCap = v)));
        dfps.add(child(() -> EssentialsConfig.dynamicFpsEnabled, new IntEntry("Minimized FPS Cap", () -> EssentialsConfig.dynamicFpsMinimizedCap, v -> EssentialsConfig.dynamicFpsMinimizedCap = v)));
        categories.put("Dynamic FPS", dfps);

        // Borderless
        List<SettingEntry> borderless = new ArrayList<>();
        borderless.add(new BoolEntry("Enable Borderless Mode", () -> EssentialsConfig.borderlessEnabled, v -> EssentialsConfig.borderlessEnabled = v));
        categories.put("Borderless", borderless);
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

                // Tooltip when hovered
                if (hovered && setting.getTooltip() != null) {
                    String tip = setting.getTooltip();
                    int tipW = font.width(tip);
                    int tipX = mouseX + 8;
                    int tipY = mouseY - 10;
                    graphics.fill(tipX - 2, tipY - 2, tipX + tipW + 2, tipY + 10, 0xE0000000);
                    graphics.text(font, tip, tipX, tipY, 0xFFCCCCCC);
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
