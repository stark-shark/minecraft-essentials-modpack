package com.essentials.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.component.ItemContainerContents;
import org.jetbrains.annotations.Nullable;

public record ShulkerBoxContentsTooltip(ItemContainerContents contents, @Nullable DyeColor color) implements TooltipComponent {
}
