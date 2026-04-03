package com.essentials.mixin.statuseffect;

import com.essentials.statuseffect.StatusEffectOverlay;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class EffectBarMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "extractEffects", at = @At("TAIL"))
    private void afterExtractEffects(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        StatusEffectOverlay.renderBars(graphics, this.minecraft);
    }
}
