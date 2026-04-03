package com.essentials.mixin.pickup;

import com.essentials.autoclicker.AutoClickHandler;
import com.essentials.config.EssentialsConfig;
import com.essentials.hud.CoordsHud;
import com.essentials.hud.DurabilityHud;
import com.essentials.pickup.PickupNotifier;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class PickupHudMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void onExtractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        PickupNotifier.render(graphics, this.minecraft);
        CoordsHud.render(graphics, this.minecraft);
        DurabilityHud.render(graphics, this.minecraft);

        // Auto-clicker indicator — top-right of crosshair
        if (AutoClickHandler.isActive() && this.minecraft.screen == null && !this.minecraft.options.hideGui) {
            Font font = this.minecraft.font;
            int cx = graphics.guiWidth() / 2;
            int cy = graphics.guiHeight() / 2;

            String button = EssentialsConfig.autoClickButton == 0 ? "L" : "R";
            String mode = EssentialsConfig.autoClickMode == 0 ? "HOLD" : EssentialsConfig.autoClickCPS + "CPS";
            String text = "AUTO [" + button + "] " + mode;

            org.joml.Matrix3x2fStack pose = graphics.pose();
            pose.pushMatrix();
            pose.scale(0.5f, 0.5f);

            int scaledCx = cx * 2 + 16;
            int scaledCy = cy * 2 - 24;
            int textW = font.width(text);

            graphics.fill(scaledCx - 1, scaledCy - 1, scaledCx + textW + 1, scaledCy + 10, 0x80000000);
            graphics.text(font, text, scaledCx, scaledCy, 0xFFFF4444, true);

            pose.popMatrix();
        }
    }
}
