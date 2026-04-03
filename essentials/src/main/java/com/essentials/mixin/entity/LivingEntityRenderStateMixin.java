package com.essentials.mixin.entity;

import com.essentials.entity.HealthBarData;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin implements HealthBarData {

    @Unique private float essentials$health = 0;
    @Unique private float essentials$maxHealth = 0;
    @Unique private boolean essentials$isPlayer = false;
    @Unique private boolean essentials$isLocalPlayer = false;

    @Override public float essentials$getHealth() { return essentials$health; }
    @Override public void essentials$setHealth(float health) { this.essentials$health = health; }
    @Override public float essentials$getMaxHealth() { return essentials$maxHealth; }
    @Override public void essentials$setMaxHealth(float maxHealth) { this.essentials$maxHealth = maxHealth; }
    @Override public boolean essentials$isPlayer() { return essentials$isPlayer; }
    @Override public void essentials$setIsPlayer(boolean isPlayer) { this.essentials$isPlayer = isPlayer; }
    @Override public boolean essentials$isLocalPlayer() { return essentials$isLocalPlayer; }
    @Override public void essentials$setIsLocalPlayer(boolean isLocalPlayer) { this.essentials$isLocalPlayer = isLocalPlayer; }
}
