package com.essentials.mixin.performance;

import com.essentials.config.EssentialsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class DynamicFpsMixin {

    @Shadow
    public abstract boolean isWindowActive();

    @Shadow
    @Final
    private com.mojang.blaze3d.platform.Window window;

    @Unique
    private long essentials$lastRenderTime = 0;

    @Inject(method = "renderFrame", at = @At("HEAD"), cancellable = true)
    private void onRenderFrame(boolean advanceGameTime, CallbackInfo ci) {
        if (!EssentialsConfig.dynamicFpsEnabled) {
            return;
        }

        if (isWindowActive()) {
            // Window is focused — render normally
            essentials$lastRenderTime = 0;
            return;
        }

        // Determine target FPS based on window state
        boolean minimized = org.lwjgl.glfw.GLFW.glfwGetWindowAttrib(window.handle(), org.lwjgl.glfw.GLFW.GLFW_ICONIFIED) != 0;
        int targetFps = minimized ? EssentialsConfig.dynamicFpsMinimizedCap : EssentialsConfig.dynamicFpsUnfocusedCap;

        if (targetFps <= 0) {
            // 0 FPS = skip all rendering
            ci.cancel();
            return;
        }

        // Throttle by checking elapsed time
        long now = Util.getEpochMillis();
        long frameTimeMs = 1000L / targetFps;

        if (essentials$lastRenderTime != 0 && (now - essentials$lastRenderTime) < frameTimeMs) {
            // Not enough time has passed — skip this frame
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
            }
            ci.cancel();
            return;
        }

        essentials$lastRenderTime = now;
    }
}
