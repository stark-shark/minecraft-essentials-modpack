package com.essentials.tooltip;

import com.essentials.config.EssentialsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.equipment.Equippable;

import java.util.List;
import java.util.Optional;

public final class EnhancedItemTooltips {

    public static void register() {
        net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
            if (!EssentialsConfig.enhancedTooltipsEnabled) return;

            boolean showTool = EssentialsConfig.showToolStats && ItemTypeStats.showToolStats(stack);
            boolean showWeapon = EssentialsConfig.showWeaponStats && ItemTypeStats.showWeaponStats(stack);
            boolean hasFood = stack.has(DataComponents.FOOD) && EssentialsConfig.showFoodStats;
            boolean hasEquipment = stack.has(DataComponents.EQUIPPABLE) && EssentialsConfig.showToolStats;
            boolean isDurableItem = stack.isDamageableItem()
                    && !showTool && !showWeapon && !hasEquipment
                    && (stack.has(DataComponents.TOOL) || stack.has(DataComponents.WEAPON)
                        || stack.has(DataComponents.EQUIPPABLE) || stack.has(DataComponents.CHARGED_PROJECTILES));

            // Strip vanilla "When in ..." attribute section for items we enhance
            if (showTool || showWeapon || hasEquipment) {
                stripVanillaAttributes(lines);
            }

            if (showTool) appendToolInfo(stack, lines);
            if (showTool && showWeapon) {
                lines.add(Component.literal("──────────────────").withStyle(ChatFormatting.DARK_GRAY));
            }
            if (showWeapon) appendWeaponInfo(stack, lines, showTool);
            if (hasEquipment) appendEquipmentInfo(stack, lines);
            if (isDurableItem) appendStandaloneDurability(stack, lines);
            if (hasFood) appendFoodInfo(stack, lines);
        });
    }

    private static void stripVanillaAttributes(List<Component> lines) {
        int removeStart = -1;
        for (int i = 0; i < lines.size(); i++) {
            String text = lines.get(i).getString();
            if (text.startsWith("When in ") || text.startsWith("When on ")) {
                removeStart = i;
                if (removeStart > 0 && lines.get(removeStart - 1).getString().isEmpty()) {
                    removeStart--;
                }
                break;
            }
        }
        if (removeStart == -1) return;

        int removeEnd = removeStart;
        for (int i = removeStart; i < lines.size(); i++) {
            removeEnd = i + 1;
            String text = lines.get(i).getString();
            if (i > removeStart + 1 && text.isEmpty()) {
                break;
            }
        }
        lines.subList(removeStart, removeEnd).clear();
    }

    private static void appendToolInfo(ItemStack stack, List<Component> lines) {
        Tool tool = stack.get(DataComponents.TOOL);
        if (tool == null) return;

        float baseSpeed = tool.defaultMiningSpeed();
        for (Tool.Rule rule : tool.rules()) {
            if (rule.speed().isPresent()) {
                baseSpeed = Math.max(baseSpeed, rule.speed().get());
            }
        }

        float enchantedSpeed = baseSpeed;
        int effLevel = getEfficiencyLevel(stack);
        if (effLevel > 0 && baseSpeed > 1.0f) {
            enchantedSpeed = baseSpeed + (effLevel * effLevel + 1);
        }

        lines.add(Component.empty());
        if (effLevel > 0 && enchantedSpeed != baseSpeed) {
            lines.add(Component.literal("Mining Speed: ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.format("%.1f", baseSpeed))
                            .withStyle(ChatFormatting.AQUA))
                    .append(Component.literal(" \u2192 ")
                            .withStyle(ChatFormatting.DARK_GRAY))
                    .append(Component.literal(String.format("%.1f", enchantedSpeed))
                            .withStyle(ChatFormatting.GOLD)));
        } else {
            lines.add(Component.literal("Mining Speed: ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.format("%.1f", baseSpeed))
                            .withStyle(ChatFormatting.AQUA)));
        }

        appendDurability(stack, lines);
    }

    private static int getEfficiencyLevel(ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return 0;

        Optional<Registry<Enchantment>> registryOpt = mc.level.registryAccess().lookup(Registries.ENCHANTMENT);
        if (registryOpt.isEmpty()) return 0;

        Registry<Enchantment> registry = registryOpt.get();
        Optional<Holder.Reference<Enchantment>> holderOpt = registry.get(Enchantments.EFFICIENCY);
        if (holderOpt.isEmpty()) return 0;

        return EnchantmentHelper.getItemEnchantmentLevel(holderOpt.get(), stack);
    }

    private static void appendWeaponInfo(ItemStack stack, List<Component> lines, boolean followsTool) {
        ItemAttributeModifiers attrs = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (attrs == null) return;
        if (!stack.has(DataComponents.WEAPON)) return;

        final double[] attackDamage = {0};
        final double[] attackSpeed = {0};
        boolean[] found = {false};

        attrs.forEach(EquipmentSlotGroup.MAINHAND, (attribute, modifier) -> {
            if (attribute.equals(Attributes.ATTACK_DAMAGE)) {
                attackDamage[0] += modifier.amount();
                found[0] = true;
            }
            if (attribute.equals(Attributes.ATTACK_SPEED)) {
                attackSpeed[0] += modifier.amount();
                found[0] = true;
            }
        });

        if (!found[0]) return;

        double totalDamage = 1.0 + attackDamage[0];
        double totalSpeed = 4.0 + attackSpeed[0];
        double dps = totalDamage * totalSpeed;

        if (!followsTool) {
            lines.add(Component.empty());
        }
        lines.add(Component.literal("Attack Damage: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(String.format("%.1f", totalDamage))
                        .withStyle(ChatFormatting.RED)));
        lines.add(Component.literal("Attack Speed: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(String.format("%.1f", totalSpeed))
                        .withStyle(ChatFormatting.YELLOW)));
        lines.add(Component.literal("DPS: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(String.format("%.1f", dps))
                        .withStyle(ChatFormatting.LIGHT_PURPLE)));

        // Always show durability for weapons
        if (!followsTool) {
            // Only append if tool section didn't already show it
            appendDurability(stack, lines);
        }
    }

    private static void appendEquipmentInfo(ItemStack stack, List<Component> lines) {
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null) return;

        // Get armor/toughness from attributes
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

        lines.add(Component.empty());

        if (armor[0] > 0) {
            lines.add(Component.literal("Armor: ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.format("%.0f", armor[0]))
                            .withStyle(ChatFormatting.BLUE)));
        }
        if (toughness[0] > 0) {
            lines.add(Component.literal("Toughness: ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.format("%.0f", toughness[0]))
                            .withStyle(ChatFormatting.DARK_AQUA)));
        }
        if (knockbackRes[0] > 0) {
            lines.add(Component.literal("Knockback Resistance: ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.format("%.0f%%", knockbackRes[0] * 100))
                            .withStyle(ChatFormatting.GOLD)));
        }

        appendDurability(stack, lines);
    }

    /**
     * For items that have durability but don't fit neatly into tool/weapon/armor
     * (e.g., bows, crossbows, fishing rods, tridents, shields, elytra).
     */
    private static void appendStandaloneDurability(ItemStack stack, List<Component> lines) {
        lines.add(Component.empty());
        appendDurability(stack, lines);
    }

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

        lines.add(Component.literal("Durability: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(remaining + "/" + maxDamage)
                        .withStyle(durColor))
                .append(Component.literal(String.format(" (%.0f%%)", pct))
                        .withStyle(ChatFormatting.DARK_GRAY)));
    }

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
        lines.add(Component.literal("Hunger: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(hungerBar.toString() + " ")
                        .withStyle(ChatFormatting.GOLD))
                .append(Component.literal("(" + nutrition + ")")
                        .withStyle(ChatFormatting.DARK_GRAY)));

        String quality;
        ChatFormatting qualityColor;
        if (saturation >= 10.0f) { quality = "Excellent"; qualityColor = ChatFormatting.GREEN; }
        else if (saturation >= 5.0f) { quality = "Good"; qualityColor = ChatFormatting.YELLOW; }
        else if (saturation >= 2.0f) { quality = "Low"; qualityColor = ChatFormatting.GOLD; }
        else { quality = "Poor"; qualityColor = ChatFormatting.RED; }

        lines.add(Component.literal("Saturation: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(String.format("%.1f ", saturation))
                        .withStyle(qualityColor))
                .append(Component.literal("(" + quality + ")")
                        .withStyle(qualityColor)));

        if (food.canAlwaysEat()) {
            lines.add(Component.literal("Can eat when full")
                    .withStyle(ChatFormatting.DARK_GREEN));
        }
    }
}
