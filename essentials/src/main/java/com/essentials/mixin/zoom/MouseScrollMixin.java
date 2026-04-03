package com.essentials.mixin.zoom;

import com.essentials.zoom.ZoomHandler;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseScrollMixin {

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void onScroll(long windowPointer, double xOffset, double yOffset, CallbackInfo ci) {
        if (ZoomHandler.isZooming()) {
            ZoomHandler.onScrollWheel(yOffset);
            ci.cancel();
        }
    }
}
