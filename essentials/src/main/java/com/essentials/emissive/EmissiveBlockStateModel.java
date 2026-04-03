package com.essentials.emissive;

import com.essentials.config.EssentialsConfig;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Wraps a BlockStateModel to make quads with emissive textures render at full brightness.
 * Works by replacing BakedQuads that have registered _e.png variants with copies
 * that have lightEmission set to 15 (max).
 */
public class EmissiveBlockStateModel implements BlockStateModel {

    private final BlockStateModel delegate;

    public EmissiveBlockStateModel(BlockStateModel delegate) {
        this.delegate = delegate;
    }

    @Override
    public void collectParts(RandomSource random, List<BlockStateModelPart> parts) {
        if (!EssentialsConfig.emissiveEnabled || !EssentialsConfig.emissiveBlocks
                || EmissiveTextureRegistry.getEmissiveMap().isEmpty()) {
            delegate.collectParts(random, parts);
            return;
        }

        // Collect original parts
        List<BlockStateModelPart> originalParts = new ArrayList<>();
        delegate.collectParts(random, originalParts);

        // Wrap parts that contain emissive quads
        for (BlockStateModelPart part : originalParts) {
            parts.add(new EmissiveBlockStateModelPart(part));
        }
    }

    @Override
    public Material.Baked particleMaterial() {
        return delegate.particleMaterial();
    }

    @Override
    public int materialFlags() {
        return delegate.materialFlags();
    }

    /**
     * Wraps a BlockStateModelPart to replace emissive quads with fullbright versions.
     */
    private static class EmissiveBlockStateModelPart implements BlockStateModelPart {
        private final BlockStateModelPart delegate;

        EmissiveBlockStateModelPart(BlockStateModelPart delegate) {
            this.delegate = delegate;
        }

        @Override
        public List<BakedQuad> getQuads(Direction face) {
            List<BakedQuad> quads = delegate.getQuads(face);
            if (quads.isEmpty()) return quads;

            boolean needsTransform = false;
            for (BakedQuad quad : quads) {
                TextureAtlasSprite sprite = quad.materialInfo().sprite();
                if (sprite != null && EmissiveTextureRegistry.hasEmissive(sprite.contents().name())) {
                    needsTransform = true;
                    break;
                }
            }

            if (!needsTransform) return quads;

            List<BakedQuad> result = new ArrayList<>(quads.size());
            for (BakedQuad quad : quads) {
                TextureAtlasSprite sprite = quad.materialInfo().sprite();
                if (sprite != null && EmissiveTextureRegistry.hasEmissive(sprite.contents().name())) {
                    // Create new MaterialInfo with max light emission
                    BakedQuad.MaterialInfo origMat = quad.materialInfo();
                    BakedQuad.MaterialInfo emissiveMat = new BakedQuad.MaterialInfo(
                            origMat.sprite(), origMat.layer(), origMat.itemRenderType(),
                            origMat.tintIndex(), false, 15 // shade=false, lightEmission=15
                    );
                    // Create new quad with emissive material
                    result.add(new BakedQuad(
                            quad.position0(), quad.position1(), quad.position2(), quad.position3(),
                            quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(),
                            quad.direction(), emissiveMat
                    ));
                } else {
                    result.add(quad);
                }
            }
            return result;
        }

        @Override
        public boolean useAmbientOcclusion() {
            return delegate.useAmbientOcclusion();
        }

        @Override
        public Material.Baked particleMaterial() {
            return delegate.particleMaterial();
        }

        @Override
        public int materialFlags() {
            return delegate.materialFlags();
        }
    }
}
