package com.essentials.zoom;

import com.essentials.config.EssentialsConfig;
import com.essentials.keybind.ModKeybinds;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public class ZoomHandler {

    private static boolean wasZooming = false;
    private static float currentFovMultiplier = 1.0f;
    private static float targetFovMultiplier = 1.0f;
    private static float zoomFov = EssentialsConfig.zoomDefaultFov;
    private static boolean originalSmoothCamera = false;

    public static boolean isZooming() {
        return EssentialsConfig.zoomEnabled && ModKeybinds.zoomKey.isDown();
    }

    public static void onScrollWheel(double delta) {
        if (!isZooming()) return;
        zoomFov -= (float) (delta * EssentialsConfig.zoomScrollSensitivity);
        zoomFov = Mth.clamp(zoomFov, EssentialsConfig.zoomMinFov, EssentialsConfig.zoomMaxFov);
    }

    public static float getModifiedFov(float originalFov) {
        boolean zooming = isZooming();

        // Handle zoom start/stop transitions
        if (zooming && !wasZooming) {
            // Zoom starting
            if (EssentialsConfig.zoomSmoothCamera) {
                originalSmoothCamera = Minecraft.getInstance().options.smoothCamera;
                Minecraft.getInstance().options.smoothCamera = true;
            }
            zoomFov = EssentialsConfig.zoomDefaultFov;
            targetFovMultiplier = zoomFov / originalFov;
        } else if (!zooming && wasZooming) {
            // Zoom stopping
            if (EssentialsConfig.zoomSmoothCamera) {
                Minecraft.getInstance().options.smoothCamera = originalSmoothCamera;
            }
            targetFovMultiplier = 1.0f;
        } else if (zooming) {
            // Currently zooming — update target based on scroll adjustments
            targetFovMultiplier = zoomFov / originalFov;
        }

        wasZooming = zooming;

        // Smooth lerp toward target
        float speed = EssentialsConfig.zoomTransitionSpeed;
        currentFovMultiplier = Mth.lerp(speed, currentFovMultiplier, targetFovMultiplier);

        // Snap if close enough
        if (Math.abs(currentFovMultiplier - targetFovMultiplier) < 0.001f) {
            currentFovMultiplier = targetFovMultiplier;
        }

        return originalFov * currentFovMultiplier;
    }

    public static float getSensitivityMultiplier() {
        if (!isZooming() || !EssentialsConfig.zoomReduceSensitivity) {
            return 1.0f;
        }
        return currentFovMultiplier * 1.1f;
    }
}
