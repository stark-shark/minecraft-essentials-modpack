package com.essentials.mixin.tooltip;

import com.essentials.config.EssentialsConfig;
import com.essentials.tooltip.EnderChestCache;
import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEnderChestContainer.class)
public abstract class EnderChestCaptureMixin extends SimpleContainer {

    @Inject(method = "stopOpen", at = @At("HEAD"))
    private void essentials$captureOnClose(ContainerUser user, CallbackInfo ci) {
        if (EssentialsConfig.enderChestTooltipEnabled) {
            EnderChestCache.capture(this.getItems());
        }
    }

    @Inject(method = "startOpen", at = @At("TAIL"))
    private void essentials$captureOnOpen(ContainerUser user, CallbackInfo ci) {
        if (EssentialsConfig.enderChestTooltipEnabled) {
            EnderChestCache.capture(this.getItems());
        }
    }
}
