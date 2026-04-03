package com.essentials.mixin.rendering;

import com.essentials.config.EssentialsConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public class FogMixin {

    @Inject(method = "setupFog", at = @At("RETURN"))
    private void onSetupFog(Camera camera, int renderDistance, DeltaTracker deltaTracker,
                            float partialTick, ClientLevel level, CallbackInfoReturnable<FogData> cir) {
        if (!EssentialsConfig.fogEnabled || !EssentialsConfig.fogDisableDistance) {
            return;
        }

        FogData data = cir.getReturnValue();
        if (data == null) {
            return;
        }

        float multiplier = EssentialsConfig.fogDistanceMultiplier;
        if (multiplier <= 0.0f) {
            data.renderDistanceStart = 1e5f;
            data.renderDistanceEnd = 1e5f;
        } else if (multiplier != 1.0f) {
            data.renderDistanceStart *= multiplier;
            data.renderDistanceEnd *= multiplier;
        }
    }
}
