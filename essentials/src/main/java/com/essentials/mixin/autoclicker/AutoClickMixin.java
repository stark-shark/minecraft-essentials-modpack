package com.essentials.mixin.autoclicker;

import com.essentials.autoclicker.AutoClickHandler;
import com.essentials.hud.DeathWaypoint;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class AutoClickMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Minecraft mc = (Minecraft) (Object) this;
        AutoClickHandler.tick(mc);
        DeathWaypoint.tick(mc);
    }
}
