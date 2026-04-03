package com.essentials.mixin.zoom;

import com.essentials.zoom.ZoomHandler;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseSensitivityMixin {

    @Shadow
    private double accumulatedDX;

    @Shadow
    private double accumulatedDY;

    @Unique
    private double essentials$originalDX;
    @Unique
    private double essentials$originalDY;

    @Inject(method = "turnPlayer", at = @At("HEAD"))
    private void beforeTurnPlayer(double movementTime, CallbackInfo ci) {
        float multiplier = ZoomHandler.getSensitivityMultiplier();
        if (multiplier != 1.0f) {
            essentials$originalDX = accumulatedDX;
            essentials$originalDY = accumulatedDY;
            accumulatedDX *= multiplier;
            accumulatedDY *= multiplier;
        }
    }

    @Inject(method = "turnPlayer", at = @At("RETURN"))
    private void afterTurnPlayer(double movementTime, CallbackInfo ci) {
        // accumulatedDX/DY get zeroed after turnPlayer anyway,
        // so no need to restore — but in case the contract changes:
        float multiplier = ZoomHandler.getSensitivityMultiplier();
        if (multiplier != 1.0f) {
            accumulatedDX = essentials$originalDX;
            accumulatedDY = essentials$originalDY;
        }
    }
}
