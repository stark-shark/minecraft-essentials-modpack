package com.essentials.mixin.pickup;

import com.essentials.pickup.PickupNotifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(ClientPacketListener.class)
public class PickupDetectMixin {

    @Unique
    private final Map<Integer, Long> essentials$handledEntities = new HashMap<>();

    @Inject(method = "handleTakeItemEntity", at = @At("HEAD"))
    private void onTakeItemEntity(ClientboundTakeItemEntityPacket packet, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        if (packet.getPlayerId() != mc.player.getId()) return;

        // Purge old entries
        long now = System.currentTimeMillis();
        essentials$handledEntities.values().removeIf(time -> now - time > 4000);

        // Skip if already handled this entity
        int entityId = packet.getItemId();
        if (essentials$handledEntities.containsKey(entityId)) return;
        essentials$handledEntities.put(entityId, now);

        Entity entity = mc.level.getEntity(entityId);
        if (entity instanceof ItemEntity itemEntity) {
            ItemStack stack = itemEntity.getItem();
            if (!stack.isEmpty()) {
                PickupNotifier.onItemPickedUp(stack, packet.getAmount());
            }
        }
    }
}
