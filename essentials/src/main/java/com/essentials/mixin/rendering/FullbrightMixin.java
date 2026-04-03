package com.essentials.mixin.rendering;

import com.essentials.config.EssentialsConfig;
import com.essentials.keybind.ModKeybinds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Lightmap;
import net.minecraft.client.renderer.state.LightmapRenderState;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Lightmap.class)
public class FullbrightMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(LightmapRenderState state, CallbackInfo ci) {
        // Check for keybind toggle (done here because it fires every frame on the render thread)
        if (EssentialsConfig.fullbrightEnabled && ModKeybinds.fullbrightKey.consumeClick()) {
            EssentialsConfig.fullbrightActive = !EssentialsConfig.fullbrightActive;
        }

        if (!EssentialsConfig.fullbrightEnabled || !EssentialsConfig.fullbrightActive) {
            return;
        }

        // Force max brightness by overriding the lightmap state
        state.needsUpdate = true;
        state.brightness = 1.0f;
        state.blockFactor = 1.0f;
        state.skyFactor = 1.0f;
        state.blockLightTint = new Vector3f(1.0f, 1.0f, 1.0f);
        state.skyLightColor = new Vector3f(1.0f, 1.0f, 1.0f);
        state.ambientColor = new Vector3f(1.0f, 1.0f, 1.0f);
        state.darknessEffectScale = 0.0f;
        state.nightVisionEffectIntensity = 1.0f;
        state.nightVisionColor = new Vector3f(1.0f, 1.0f, 1.0f);
        state.bossOverlayWorldDarkening = 0.0f;
    }
}
