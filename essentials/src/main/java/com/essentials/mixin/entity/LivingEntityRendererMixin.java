package com.essentials.mixin.entity;

import com.essentials.entity.HealthBarData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState> {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
    private void onExtractRenderState(T entity, S state, float partialTicks, CallbackInfo ci) {
        if (state instanceof HealthBarData data) {
            data.essentials$setHealth(entity.getHealth());
            data.essentials$setMaxHealth(entity.getMaxHealth());
            // Mark as player, but also check if it's the LOCAL player
            boolean isPlayer = entity instanceof Player;
            boolean isLocalPlayer = entity == Minecraft.getInstance().player;
            data.essentials$setIsPlayer(isPlayer);
            data.essentials$setIsLocalPlayer(isLocalPlayer);
        }
    }
}
