package com.essentials.tooltip;

import com.essentials.config.EssentialsConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.ShovelItem;

/**
 * Determines which stats (tool, weapon, or both) to show for a given item,
 * based on per-item-type user config.
 */
public final class ItemTypeStats {

    // Stat modes: 0 = tool only, 1 = weapon only, 2 = both
    public static final int TOOL_ONLY = 0;
    public static final int WEAPON_ONLY = 1;
    public static final int BOTH = 2;

    public static boolean showToolStats(ItemStack stack) {
        if (!stack.has(DataComponents.TOOL)) return false;
        int mode = getStatMode(stack);
        return mode == TOOL_ONLY || mode == BOTH;
    }

    public static boolean showWeaponStats(ItemStack stack) {
        if (!stack.has(DataComponents.WEAPON)) return false;
        int mode = getStatMode(stack);
        return mode == WEAPON_ONLY || mode == BOTH;
    }

    public static int getStatMode(ItemStack stack) {
        String type = detectType(stack);
        return switch (type) {
            case "pickaxe" -> EssentialsConfig.pickaxeStatMode;
            case "axe" -> EssentialsConfig.axeStatMode;
            case "shovel" -> EssentialsConfig.shovelStatMode;
            case "hoe" -> EssentialsConfig.hoeStatMode;
            case "sword" -> EssentialsConfig.swordStatMode;
            case "mace" -> EssentialsConfig.maceStatMode;
            default -> {
                // Fallback: show whatever components the item has
                boolean hasTool = stack.has(DataComponents.TOOL);
                boolean hasWeapon = stack.has(DataComponents.WEAPON);
                if (hasTool && hasWeapon) yield BOTH;
                if (hasTool) yield TOOL_ONLY;
                yield WEAPON_ONLY;
            }
        };
    }

    public static String detectType(ItemStack stack) {
        // Check specific item classes first
        if (stack.getItem() instanceof AxeItem) return "axe";
        if (stack.getItem() instanceof ShovelItem) return "shovel";
        if (stack.getItem() instanceof HoeItem) return "hoe";
        if (stack.getItem() instanceof MaceItem) return "mace";

        // For items without dedicated classes (swords, pickaxes), check item ID
        String id = stack.getItem().builtInRegistryHolder().key().identifier().getPath();
        if (id.contains("pickaxe")) return "pickaxe";
        if (id.contains("sword")) return "sword";

        // Generic fallback
        if (stack.has(DataComponents.WEAPON) && !stack.has(DataComponents.TOOL)) return "sword";
        if (stack.has(DataComponents.TOOL) && !stack.has(DataComponents.WEAPON)) return "pickaxe";
        return "other";
    }

    public static String modeLabel(int mode) {
        return switch (mode) {
            case TOOL_ONLY -> "Tool";
            case WEAPON_ONLY -> "Weapon";
            case BOTH -> "Both";
            default -> "Tool";
        };
    }

    public static int cycleMode(int current) {
        return (current + 1) % 3;
    }
}
