package com.essentials.tooltip;

import com.essentials.config.EssentialsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;

public final class EnhancedItemTooltips {

    public static void register() {
        net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
            if (!EssentialsConfig.enhancedTooltipsEnabled) return;

            boolean showTool = EssentialsConfig.showToolStats && ItemTypeStats.showToolStats(stack);
            boolean showWeapon = EssentialsConfig.showWeaponStats && ItemTypeStats.showWeaponStats(stack);
            boolean hasFood = stack.has(DataComponents.FOOD) && EssentialsConfig.showFoodStats;
            boolean hasEquipment = stack.has(DataComponents.EQUIPPABLE) && EssentialsConfig.showToolStats;
            boolean isBaseTool = isBaseToolType(stack);
            boolean isRanged = isRangedWeapon(stack) && EssentialsConfig.showWeaponStats;

            // Strip ALL vanilla "When in/on ..." sections for any item we enhance
            if (showTool || showWeapon || hasEquipment || isRanged || (stack.isDamageableItem() && hasAnyEnhancement(stack))) {
                stripVanillaAttributes(lines);
            }

            if (showTool) appendToolInfo(stack, lines, isBaseTool && !showWeapon);
            if (showTool && showWeapon) {
                lines.add(Component.literal("──────────────────").withStyle(ChatFormatting.DARK_GRAY));
            }
            if (showWeapon) appendWeaponInfo(stack, lines, showTool, !isBaseTool && !showTool);
            if (isRanged && !showWeapon) appendRangedInfo(stack, lines);
            if (hasEquipment && !showTool && !showWeapon) appendEquipmentInfo(stack, lines);

            // Standalone durability for items not covered above
            if (stack.isDamageableItem() && !showTool && !showWeapon && !hasEquipment && !isRanged) {
                appendStandaloneDurability(stack, lines);
            }

            if (hasFood) appendFoodInfo(stack, lines);
        });
    }

    /** Is this item primarily a tool (pickaxe, axe, shovel, hoe, fishing rod)? */
    private static boolean isBaseToolType(ItemStack stack) {
        String type = ItemTypeStats.detectType(stack);
        return switch (type) {
            case "pickaxe", "axe", "shovel", "hoe" -> true;
            default -> false;
        };
    }

    private static boolean isRangedWeapon(ItemStack stack) {
        String id = stack.getItem().builtInRegistryHolder().key().identifier().getPath();
        return id.equals("bow") || id.equals("crossbow");
    }

    private static boolean hasAnyEnhancement(ItemStack stack) {
        return stack.has(DataComponents.TOOL) || stack.has(DataComponents.WEAPON)
                || stack.has(DataComponents.EQUIPPABLE);
    }

    private static void stripVanillaAttributes(List<Component> lines) {
        // Remove ALL "When in/on ..." sections (Main Hand, Off Hand, Head, Chest, Legs, Feet, Body)
        boolean removing = false;
        int i = 0;
        while (i < lines.size()) {
            String text = lines.get(i).getString();
            if (text.startsWith("When in ") || text.startsWith("When on ")) {
                // Also remove the blank line before it
                if (!removing && i > 0 && lines.get(i - 1).getString().isEmpty()) {
                    lines.remove(i - 1);
                    i--;
                }
                removing = true;
            } else if (removing) {
                // Stop removing at the next blank line or non-attribute line
                if (text.isEmpty() || (!text.startsWith(" ") && !text.startsWith("+"))) {
                    removing = false;
                    continue; // Don't remove this line, check next
                }
            }

            if (removing) {
                lines.remove(i);
            } else {
                i++;
            }
        }
    }

    // ======== TOOL INFO ========

    private static void appendToolInfo(ItemStack stack, List<Component> lines, boolean showDurability) {
        Tool tool = stack.get(DataComponents.TOOL);
        if (tool == null) return;

        float baseSpeed = tool.defaultMiningSpeed();
        for (Tool.Rule rule : tool.rules()) {
            if (rule.speed().isPresent()) {
                baseSpeed = Math.max(baseSpeed, rule.speed().get());
            }
        }

        float enchantedSpeed = baseSpeed;
        int effLevel = getEnchantLevel(stack, Enchantments.EFFICIENCY);
        if (effLevel > 0 && baseSpeed > 1.0f) {
            enchantedSpeed = baseSpeed + (effLevel * effLevel + 1);
        }

        lines.add(Component.empty());
        if (effLevel > 0 && enchantedSpeed != baseSpeed) {
            lines.add(Component.literal("Mining Speed: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.format("%.1f", baseSpeed)).withStyle(ChatFormatting.AQUA))
                    .append(Component.literal(" \u2192 ").withStyle(ChatFormatting.DARK_GRAY))
                    .append(Component.literal(String.format("%.1f", enchantedSpeed)).withStyle(ChatFormatting.GOLD)));
        } else {
            lines.add(Component.literal("Mining Speed: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.format("%.1f", baseSpeed)).withStyle(ChatFormatting.AQUA)));
        }

        if (showDurability) appendDurability(stack, lines);
    }

    // ======== WEAPON INFO ========

    private static void appendWeaponInfo(ItemStack stack, List<Component> lines, boolean followsTool, boolean showDurability) {
        ItemAttributeModifiers attrs = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (attrs == null) return;
        if (!stack.has(DataComponents.WEAPON)) return;

        final double[] attackDamage = {0};
        final double[] attackSpeed = {0};
        boolean[] found = {false};

        attrs.forEach(EquipmentSlotGroup.MAINHAND, (attribute, modifier) -> {
            if (attribute.equals(Attributes.ATTACK_DAMAGE)) { attackDamage[0] += modifier.amount(); found[0] = true; }
            if (attribute.equals(Attributes.ATTACK_SPEED)) { attackSpeed[0] += modifier.amount(); found[0] = true; }
        });

        if (!found[0]) return;

        double baseDamage = 1.0 + attackDamage[0];
        double totalSpeed = 4.0 + attackSpeed[0];

        // Sharpness: +0.5 + 0.5 * level
        int sharpLevel = getEnchantLevel(stack, Enchantments.SHARPNESS);
        double enchantedDamage = baseDamage;
        if (sharpLevel > 0) enchantedDamage += 0.5 + 0.5 * sharpLevel;

        // Smite/Bane (show as conditional bonuses if present)
        int smiteLevel = getEnchantLevel(stack, Enchantments.SMITE);
        int baneLevel = getEnchantLevel(stack, Enchantments.BANE_OF_ARTHROPODS);

        if (!followsTool) lines.add(Component.empty());

        // Attack Damage with enchant bonus
        if (sharpLevel > 0) {
            lines.add(Component.literal("Attack Damage: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.format("%.1f", baseDamage)).withStyle(ChatFormatting.RED))
                    .append(Component.literal(" \u2192 ").withStyle(ChatFormatting.DARK_GRAY))
                    .append(Component.literal(String.format("%.1f", enchantedDamage)).withStyle(ChatFormatting.GOLD)));
        } else {
            lines.add(Component.literal("Attack Damage: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.format("%.1f", baseDamage)).withStyle(ChatFormatting.RED)));
        }

        if (smiteLevel > 0) {
            double smiteBonus = 2.5 * smiteLevel;
            lines.add(Component.literal("  vs Undead: ").withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.literal(String.format("+%.1f", smiteBonus)).withStyle(ChatFormatting.GOLD)));
        }
        if (baneLevel > 0) {
            double baneBonus = 2.5 * baneLevel;
            lines.add(Component.literal("  vs Arthropods: ").withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.literal(String.format("+%.1f", baneBonus)).withStyle(ChatFormatting.GOLD)));
        }

        lines.add(Component.literal("Attack Speed: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(String.format("%.1f", totalSpeed)).withStyle(ChatFormatting.YELLOW)));

        double dps = enchantedDamage * totalSpeed;
        lines.add(Component.literal("DPS: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(String.format("%.1f", dps)).withStyle(ChatFormatting.LIGHT_PURPLE)));

        if (showDurability) appendDurability(stack, lines);
    }

    // ======== RANGED WEAPON INFO (BOW / CROSSBOW) ========

    private static void appendRangedInfo(ItemStack stack, List<Component> lines) {
        String id = stack.getItem().builtInRegistryHolder().key().identifier().getPath();
        boolean isBow = id.equals("bow");
        boolean isCrossbow = id.equals("crossbow");

        lines.add(Component.empty());

        if (isBow) {
            // Bow: base arrow damage is 6.0 at full charge
            double baseDamage = 6.0;
            int powerLevel = getEnchantLevel(stack, Enchantments.POWER);
            if (powerLevel > 0) {
                // Power formula: damage * (1 + 0.25 * (level + 1))
                double enchantedDamage = baseDamage * (1.0 + 0.25 * (powerLevel + 1));
                lines.add(Component.literal("Arrow Damage: ").withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(String.format("%.1f", baseDamage)).withStyle(ChatFormatting.RED))
                        .append(Component.literal(" \u2192 ").withStyle(ChatFormatting.DARK_GRAY))
                        .append(Component.literal(String.format("%.1f", enchantedDamage)).withStyle(ChatFormatting.GOLD)));
            } else {
                lines.add(Component.literal("Arrow Damage: ").withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(String.format("%.1f", baseDamage)).withStyle(ChatFormatting.RED)));
            }

            int punchLevel = getEnchantLevel(stack, Enchantments.PUNCH);
            if (punchLevel > 0) {
                // Punch adds 3 blocks of knockback per level
                lines.add(Component.literal("Knockback: ").withStyle(ChatFormatting.GRAY)
                        .append(Component.literal("+" + (punchLevel * 3) + " blocks").withStyle(ChatFormatting.AQUA)));
            }
        }

        if (isCrossbow) {
            // Crossbow: base arrow damage is 9.0
            double baseDamage = 9.0;
            lines.add(Component.literal("Arrow Damage: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.format("%.1f", baseDamage)).withStyle(ChatFormatting.RED)));

            int quickChargeLevel = getEnchantLevel(stack, Enchantments.QUICK_CHARGE);
            if (quickChargeLevel > 0) {
                // Base charge time: 1.25s, each level reduces by 0.25s
                double chargeTime = 1.25 - (quickChargeLevel * 0.25);
                lines.add(Component.literal("Charge Time: ").withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(String.format("%.2fs", Math.max(0, chargeTime))).withStyle(ChatFormatting.AQUA)));
            }

            int piercingLevel = getEnchantLevel(stack, Enchantments.PIERCING);
            if (piercingLevel > 0) {
                lines.add(Component.literal("Piercing: ").withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(piercingLevel + " entities").withStyle(ChatFormatting.AQUA)));
            }
        }

        appendDurability(stack, lines);
    }

    // ======== EQUIPMENT INFO (ARMOR / SHIELDS / ELYTRA) ========

    private static void appendEquipmentInfo(ItemStack stack, List<Component> lines) {
        ItemAttributeModifiers attrs = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);

        final double[] armor = {0};
        final double[] toughness = {0};
        final double[] knockbackRes = {0};

        if (attrs != null) {
            attrs.forEach(EquipmentSlotGroup.ANY, (attribute, modifier) -> {
                if (attribute.equals(Attributes.ARMOR)) armor[0] += modifier.amount();
                if (attribute.equals(Attributes.ARMOR_TOUGHNESS)) toughness[0] += modifier.amount();
                if (attribute.equals(Attributes.KNOCKBACK_RESISTANCE)) knockbackRes[0] += modifier.amount();
            });
        }

        // Protection enchantment: each level = 4% damage reduction
        int protLevel = getEnchantLevel(stack, Enchantments.PROTECTION);
        int fireProtLevel = getEnchantLevel(stack, Enchantments.FIRE_PROTECTION);
        int blastProtLevel = getEnchantLevel(stack, Enchantments.BLAST_PROTECTION);
        int projProtLevel = getEnchantLevel(stack, Enchantments.PROJECTILE_PROTECTION);

        lines.add(Component.empty());

        if (armor[0] > 0) {
            lines.add(Component.literal("Armor: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.format("%.0f", armor[0])).withStyle(ChatFormatting.BLUE)));
        }
        if (toughness[0] > 0) {
            lines.add(Component.literal("Toughness: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.format("%.0f", toughness[0])).withStyle(ChatFormatting.DARK_AQUA)));
        }
        if (knockbackRes[0] > 0) {
            lines.add(Component.literal("Knockback Resistance: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.format("%.0f%%", knockbackRes[0] * 100)).withStyle(ChatFormatting.GOLD)));
        }

        // Damage reduction: armor gives ~4% per point, protection adds on top
        int baseReduction = (int) (armor[0] * 4);
        if (protLevel > 0 || fireProtLevel > 0 || blastProtLevel > 0 || projProtLevel > 0) {
            if (protLevel > 0) {
                int enhanced = baseReduction + protLevel * 4;
                lines.add(Component.literal("Damage Reduction: ").withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(baseReduction + "%").withStyle(ChatFormatting.BLUE))
                        .append(Component.literal(" \u2192 ").withStyle(ChatFormatting.DARK_GRAY))
                        .append(Component.literal(enhanced + "%").withStyle(ChatFormatting.GOLD)));
            }
            if (fireProtLevel > 0) {
                int enhanced = baseReduction + fireProtLevel * 8;
                lines.add(Component.literal("Fire Reduction: ").withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(baseReduction + "%").withStyle(ChatFormatting.BLUE))
                        .append(Component.literal(" \u2192 ").withStyle(ChatFormatting.DARK_GRAY))
                        .append(Component.literal(enhanced + "%").withStyle(ChatFormatting.GOLD)));
            }
            if (blastProtLevel > 0) {
                int enhanced = baseReduction + blastProtLevel * 8;
                lines.add(Component.literal("Blast Reduction: ").withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(baseReduction + "%").withStyle(ChatFormatting.BLUE))
                        .append(Component.literal(" \u2192 ").withStyle(ChatFormatting.DARK_GRAY))
                        .append(Component.literal(enhanced + "%").withStyle(ChatFormatting.GOLD)));
            }
            if (projProtLevel > 0) {
                int enhanced = baseReduction + projProtLevel * 8;
                lines.add(Component.literal("Projectile Reduction: ").withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(baseReduction + "%").withStyle(ChatFormatting.BLUE))
                        .append(Component.literal(" \u2192 ").withStyle(ChatFormatting.DARK_GRAY))
                        .append(Component.literal(enhanced + "%").withStyle(ChatFormatting.GOLD)));
            }
        } else if (baseReduction > 0) {
            lines.add(Component.literal("Damage Reduction: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(baseReduction + "%").withStyle(ChatFormatting.BLUE)));
        }

        appendDurability(stack, lines);
    }

    // ======== STANDALONE DURABILITY (bows, crossbows, fishing rods, shields, elytra, tridents) ========

    private static void appendStandaloneDurability(ItemStack stack, List<Component> lines) {
        if (!stack.isDamageableItem()) return;
        lines.add(Component.empty());
        appendDurability(stack, lines);
    }

    // ======== DURABILITY (shared) ========

    private static void appendDurability(ItemStack stack, List<Component> lines) {
        if (!stack.isDamageableItem()) return;

        int maxDamage = stack.getMaxDamage();
        int remaining = maxDamage - stack.getDamageValue();
        float pct = (float) remaining / maxDamage * 100f;

        ChatFormatting durColor;
        if (pct > 50) durColor = ChatFormatting.GREEN;
        else if (pct > 25) durColor = ChatFormatting.YELLOW;
        else if (pct > 10) durColor = ChatFormatting.RED;
        else durColor = ChatFormatting.DARK_RED;

        // Unbreaking: effective durability = actual * (1 + unbreaking_level) for tools
        int unbreakingLevel = getEnchantLevel(stack, Enchantments.UNBREAKING);

        if (unbreakingLevel > 0) {
            int effectiveMax = maxDamage * (1 + unbreakingLevel);
            int effectiveRemaining = remaining * (1 + unbreakingLevel);
            lines.add(Component.literal("Durability: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(remaining + "/" + maxDamage).withStyle(durColor))
                    .append(Component.literal(" \u2192 ").withStyle(ChatFormatting.DARK_GRAY))
                    .append(Component.literal("~" + effectiveRemaining + "/" + effectiveMax).withStyle(ChatFormatting.GOLD))
                    .append(Component.literal(String.format(" (%.0f%%)", pct)).withStyle(ChatFormatting.DARK_GRAY)));
        } else {
            lines.add(Component.literal("Durability: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(remaining + "/" + maxDamage).withStyle(durColor))
                    .append(Component.literal(String.format(" (%.0f%%)", pct)).withStyle(ChatFormatting.DARK_GRAY)));
        }
    }

    // ======== FOOD INFO ========

    private static void appendFoodInfo(ItemStack stack, List<Component> lines) {
        FoodProperties food = stack.get(DataComponents.FOOD);
        if (food == null) return;

        int nutrition = food.nutrition();
        float saturation = food.saturation();

        int fullPoints = nutrition / 2;
        boolean halfPoint = nutrition % 2 != 0;
        int emptyPoints = 10 - fullPoints - (halfPoint ? 1 : 0);

        StringBuilder hungerBar = new StringBuilder();
        for (int i = 0; i < fullPoints; i++) hungerBar.append("\u25C6");
        if (halfPoint) hungerBar.append("\u25C7");
        for (int i = 0; i < emptyPoints; i++) hungerBar.append("\u25CB");

        lines.add(Component.empty());
        lines.add(Component.literal("Hunger: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(hungerBar.toString() + " ").withStyle(ChatFormatting.GOLD))
                .append(Component.literal("(" + nutrition + ")").withStyle(ChatFormatting.DARK_GRAY)));

        String quality;
        ChatFormatting qualityColor;
        if (saturation >= 10.0f) { quality = "Excellent"; qualityColor = ChatFormatting.GREEN; }
        else if (saturation >= 5.0f) { quality = "Good"; qualityColor = ChatFormatting.YELLOW; }
        else if (saturation >= 2.0f) { quality = "Low"; qualityColor = ChatFormatting.GOLD; }
        else { quality = "Poor"; qualityColor = ChatFormatting.RED; }

        lines.add(Component.literal("Saturation: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(String.format("%.1f ", saturation)).withStyle(qualityColor))
                .append(Component.literal("(" + quality + ")").withStyle(qualityColor)));

        if (food.canAlwaysEat()) {
            lines.add(Component.literal("Can eat when full").withStyle(ChatFormatting.DARK_GREEN));
        }
    }

    // ======== ENCHANTMENT HELPER ========

    private static int getEnchantLevel(ItemStack stack, ResourceKey<Enchantment> key) {
        // Read directly from the item's enchantment component — no registry lookup needed
        var enchants = stack.getOrDefault(DataComponents.ENCHANTMENTS, net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY);
        for (var entry : enchants.entrySet()) {
            if (entry.getKey().is(key)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }
}
