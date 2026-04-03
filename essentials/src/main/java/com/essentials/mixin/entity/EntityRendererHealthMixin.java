package com.essentials.mixin.entity;

import com.essentials.config.EssentialsConfig;
import com.essentials.entity.HealthBarData;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class EntityRendererHealthMixin {

    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At("TAIL"))
    private void onSubmitHealthBar(LivingEntityRenderState state, PoseStack poseStack,
                                    SubmitNodeCollector collector, CameraRenderState camera, CallbackInfo ci) {
        if (!EssentialsConfig.healthBarsEnabled) return;
        if (!(state instanceof HealthBarData data)) return;
        if (data.essentials$getMaxHealth() <= 0) return;

        float health = data.essentials$getHealth();
        float maxHealth = data.essentials$getMaxHealth();
        if (health <= 0) return;

        // Never show for the local player
        if (data.essentials$isLocalPlayer()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) return;

        boolean isPlayer = data.essentials$isPlayer();
        if (isPlayer) {
            if (!EssentialsConfig.healthBarsPlayers) return;
            if (health >= maxHealth && !EssentialsConfig.healthBarsPlayersFull) return;
        } else {
            if (!EssentialsConfig.healthBarsMobs) return;
            if (health >= maxHealth && !EssentialsConfig.healthBarsMobsFull) return;
        }

        Component healthBar = buildHealthBar(health, maxHealth);

        Vec3 attachment = state.nameTagAttachment;
        if (attachment == null) {
            attachment = new Vec3(0, state.boundingBoxHeight, 0);
        }

        poseStack.pushPose();
        poseStack.translate(0.0f, -0.25f, 0.0f);
        collector.submitNameTag(poseStack, attachment, 0, healthBar,
                true, state.lightCoords, state.distanceToCameraSq, camera);
        poseStack.popPose();
    }

    private static Component buildHealthBar(float health, float maxHealth) {
        float ratio = health / maxHealth;

        ChatFormatting fillColor;
        if (ratio > 0.6f) fillColor = ChatFormatting.GREEN;
        else if (ratio > 0.3f) fillColor = ChatFormatting.YELLOW;
        else fillColor = ChatFormatting.RED;

        // Dynamic block count based on max health
        int totalBlocks;
        if (maxHealth <= 10) totalBlocks = 3;        // Small mobs (chicken=4, rabbit=3)
        else if (maxHealth <= 20) totalBlocks = 5;    // Normal mobs (zombie=20, player=20)
        else if (maxHealth <= 40) totalBlocks = 7;    // Tough mobs (iron golem=100 gets 10, enderman=40)
        else if (maxHealth <= 100) totalBlocks = 10;  // Bosses
        else totalBlocks = 15;                        // Wither=300, custom bosses

        int filledBlocks = Math.max(1, Math.round(ratio * totalBlocks));

        MutableComponent bar = Component.literal("");
        for (int i = 0; i < filledBlocks; i++) {
            bar.append(Component.literal("\u2588").withStyle(fillColor));
        }
        for (int i = filledBlocks; i < totalBlocks; i++) {
            bar.append(Component.literal("\u2588").withStyle(ChatFormatting.DARK_GRAY));
        }
        return bar;
    }
}
