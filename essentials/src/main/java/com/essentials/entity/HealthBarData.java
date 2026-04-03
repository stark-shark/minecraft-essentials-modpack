package com.essentials.entity;

public interface HealthBarData {
    float essentials$getHealth();
    void essentials$setHealth(float health);
    float essentials$getMaxHealth();
    void essentials$setMaxHealth(float maxHealth);
    boolean essentials$isPlayer();
    void essentials$setIsPlayer(boolean isPlayer);
    boolean essentials$isLocalPlayer();
    void essentials$setIsLocalPlayer(boolean isLocalPlayer);
}
