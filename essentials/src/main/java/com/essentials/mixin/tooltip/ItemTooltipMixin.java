package com.essentials.mixin.tooltip;

import com.essentials.config.EssentialsConfig;
import com.essentials.tooltip.EnderChestContentsTooltip;
import com.essentials.tooltip.MapPreviewTooltip;
import com.essentials.tooltip.ShulkerBoxContentsTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.saveddata.maps.MapId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Item.class)
public class ItemTooltipMixin {

    @Inject(method = "getTooltipImage", at = @At("HEAD"), cancellable = true)
    private void essentials$injectContainerTooltip(ItemStack stack, CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
        // Shulker box tooltip
        if (EssentialsConfig.shulkerTooltipEnabled
                && stack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof ShulkerBoxBlock shulkerBox) {
            ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
            if (contents != null && !contents.equals(ItemContainerContents.EMPTY)) {
                cir.setReturnValue(Optional.of(
                        new ShulkerBoxContentsTooltip(contents, shulkerBox.getColor())
                ));
                return;
            }
        }

        // Ender chest tooltip
        if (EssentialsConfig.enderChestTooltipEnabled && stack.is(Items.ENDER_CHEST)) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                cir.setReturnValue(Optional.of(new EnderChestContentsTooltip()));
                return;
            }
        }

        // Filled map tooltip
        if (EssentialsConfig.mapTooltipEnabled && stack.getItem() instanceof MapItem) {
            MapId mapId = stack.get(DataComponents.MAP_ID);
            if (mapId != null) {
                cir.setReturnValue(Optional.of(new MapPreviewTooltip(mapId)));
            }
        }
    }
}
