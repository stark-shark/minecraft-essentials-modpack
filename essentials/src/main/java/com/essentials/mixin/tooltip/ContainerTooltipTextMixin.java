package com.essentials.mixin.tooltip;

import com.essentials.config.EssentialsConfig;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemContainerContents.class)
public class ContainerTooltipTextMixin {

    @Inject(method = "addToTooltip", at = @At("HEAD"), cancellable = true)
    private void essentials$suppressContainerText(Item.TooltipContext context, Consumer<Component> builder,
                                                   TooltipFlag flag, DataComponentGetter getter, CallbackInfo ci) {
        if (EssentialsConfig.shulkerTooltipEnabled) {
            ci.cancel();
        }
    }
}
