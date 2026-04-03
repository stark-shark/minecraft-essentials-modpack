package com.essentials.mixin.emissive;

import com.essentials.EssentialsMod;
import com.essentials.config.EssentialsConfig;
import com.essentials.emissive.EmissiveTextureRegistry;
import com.mojang.blaze3d.vertex.QuadInstance;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBlockRenderer.class)
public class BlockEmissiveMixin {

    @Shadow
    @Final
    private QuadInstance quadInstance;

    @Unique
    private static boolean essentials$debugLogged = false;

    @Inject(method = "putQuadWithTint", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/block/BlockQuadOutput;put(FFFLnet/minecraft/client/resources/model/geometry/BakedQuad;Lcom/mojang/blaze3d/vertex/QuadInstance;)V"
    ))
    private void beforePutQuad(BlockQuadOutput output, float x, float y, float z,
                                BlockAndTintGetter level, BlockState state, BlockPos pos,
                                BakedQuad quad, CallbackInfo ci) {
        if (!EssentialsConfig.emissiveEnabled || !EssentialsConfig.emissiveBlocks) return;

        TextureAtlasSprite sprite = quad.materialInfo().sprite();
        if (sprite == null) return;

        Identifier spriteId = sprite.contents().name();

        // Debug: log first few sprite IDs to compare with registry
        if (!essentials$debugLogged && EmissiveTextureRegistry.isScanned()) {
            EssentialsMod.LOGGER.info("[Essentials] Sample quad sprite ID: '{}' (block: {})", spriteId, state.getBlock());
            if (EmissiveTextureRegistry.getEmissiveMap().size() > 0) {
                Identifier firstKey = EmissiveTextureRegistry.getEmissiveMap().keySet().iterator().next();
                EssentialsMod.LOGGER.info("[Essentials] Sample registry key: '{}' (equals: {})", firstKey, firstKey.equals(spriteId));
                EssentialsMod.LOGGER.info("[Essentials] Key class: {} Sprite class: {}", firstKey.getClass().getName(), spriteId.getClass().getName());
            }
            essentials$debugLogged = true;
        }

        if (EmissiveTextureRegistry.hasEmissive(spriteId)) {
            quadInstance.setLightCoords(0xF000F0);
        }
    }
}
