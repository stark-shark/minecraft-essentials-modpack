package com.essentials.emissive;

import com.essentials.EssentialsMod;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.minecraft.resources.Identifier;

public class EmissiveModelLoadingPlugin implements ModelLoadingPlugin {

    public static void register() {
        ModelLoadingPlugin.register(new EmissiveModelLoadingPlugin());
        EssentialsMod.LOGGER.info("[Essentials] Registered emissive model loading plugin.");
    }

    @Override
    public void initialize(Context pluginContext) {
        pluginContext.modifyBlockModelAfterBake().register(
                ModelModifier.WRAP_PHASE,
                (model, context) -> {
                    if (model == null) return null;
                    // Wrap every block model with our emissive wrapper
                    return new EmissiveBlockStateModel(model);
                }
        );
    }
}
