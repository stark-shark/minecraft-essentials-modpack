package com.essentials.mixin.zoom;

import com.essentials.zoom.ZoomHandler;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraZoomMixin {

    @Inject(method = "calculateFov", at = @At("RETURN"), cancellable = true)
    private void onCalculateFov(float partialTicks, CallbackInfoReturnable<Float> cir) {
        float fov = cir.getReturnValue();
        float modified = ZoomHandler.getModifiedFov(fov);
        if (modified != fov) {
            cir.setReturnValue(modified);
        }
    }
}
