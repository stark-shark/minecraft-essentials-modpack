package com.essentials.mixin.rendering;

import com.essentials.config.EssentialsConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.LavaFogEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LavaFogEnvironment.class)
public class LavaFogMixin {

    @Inject(method = "setupFog", at = @At("TAIL"))
    private void onSetupFog(FogData fogData, Camera camera, ClientLevel level,
                            float viewDistance, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!EssentialsConfig.fogEnabled || !EssentialsConfig.fogDisableLava) {
            return;
        }

        fogData.environmentalStart = viewDistance;
        fogData.environmentalEnd = viewDistance;
        fogData.skyEnd = viewDistance;
        fogData.cloudEnd = viewDistance;
    }
}
