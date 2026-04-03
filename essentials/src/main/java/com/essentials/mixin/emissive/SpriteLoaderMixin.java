package com.essentials.mixin.emissive;

import com.essentials.emissive.EmissiveTextureRegistry;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(SpriteLoader.class)
public class SpriteLoaderMixin {

    @Inject(method = "loadAndStitch", at = @At("HEAD"))
    private void onLoadAndStitch(ResourceManager manager, Identifier atlasInfoLocation,
                                  int maxMipmapLevels, Executor taskExecutor,
                                  Set<MetadataSectionType<?>> additionalMetadata,
                                  CallbackInfoReturnable<CompletableFuture<SpriteLoader.Preparations>> cir) {
        // Scan for emissive textures on the blocks atlas load
        if (atlasInfoLocation.getPath().contains("blocks")) {
            EmissiveTextureRegistry.scan(manager);
        }
    }
}
