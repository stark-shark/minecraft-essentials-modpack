package com.essentials.mixin.inventory;

import com.essentials.inventory.MatchHighlighter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class SlotChangeMixin {

    @Inject(method = "handleContainerSetSlot", at = @At("TAIL"))
    private void essentials$onSlotUpdate(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
        if (!MatchHighlighter.isActive()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Recalculate which items still match after this slot change
        int totalSlots = mc.player.containerMenu.slots.size();
        int containerSlots = totalSlots - 36;
        if (containerSlots > 0) {
            MatchHighlighter.onSlotChanged(containerSlots);
        }
    }
}
