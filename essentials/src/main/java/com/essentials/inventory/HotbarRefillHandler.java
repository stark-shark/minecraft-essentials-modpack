package com.essentials.inventory;

import com.essentials.config.EssentialsConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;

public final class HotbarRefillHandler {

    private static final ItemStack[] lastHotbar = new ItemStack[9];
    private static final int[] lastCount = new int[9];
    private static final int[] refillCooldown = new int[9]; // Per-slot cooldown
    private static boolean initialized = false;

    // Track drop key across ticks — quick taps might release before our handler runs
    private static int dropKeyRecentTicks = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> tick(client));
    }

    private static void tick(Minecraft mc) {
        if (!EssentialsConfig.hotbarRefillEnabled) return;

        LocalPlayer player = mc.player;
        MultiPlayerGameMode gameMode = mc.gameMode;
        if (player == null || gameMode == null) return;

        // Track drop key with a 3-tick window so quick taps are caught
        if (mc.options.keyDrop.isDown()) {
            dropKeyRecentTicks = 3;
        } else if (dropKeyRecentTicks > 0) {
            dropKeyRecentTicks--;
        }

        if (mc.screen != null) {
            // Screen is open — update snapshots so we don't trigger on close
            updateSnapshots(player.getInventory());
            return;
        }

        Inventory inv = player.getInventory();

        if (!initialized) {
            updateSnapshots(inv);
            initialized = true;
            return;
        }

        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
            // Tick down per-slot cooldown
            if (refillCooldown[hotbarSlot] > 0) {
                refillCooldown[hotbarSlot]--;
                // Keep snapshot frozen during cooldown so we don't re-trigger
                lastHotbar[hotbarSlot] = inv.getItem(hotbarSlot).copy();
                lastCount[hotbarSlot] = inv.getItem(hotbarSlot).getCount();
                continue;
            }

            ItemStack current = inv.getItem(hotbarSlot);
            ItemStack previous = lastHotbar[hotbarSlot];
            int prevCount = lastCount[hotbarSlot];

            if (current.isEmpty() && !previous.isEmpty()) {
                boolean wasDropAction = dropKeyRecentTicks > 0;

                boolean shouldRefill;
                if (wasDropAction) {
                    // Q or Ctrl+Q drop: respect the refillOnDrop toggle
                    shouldRefill = EssentialsConfig.refillOnDrop;
                } else if (prevCount == 1) {
                    // Count went from 1 to 0 without drop key: natural consumption
                    shouldRefill = true;
                } else {
                    // Count went from >1 to 0 without drop key: manual move/swap
                    shouldRefill = false;
                }

                if (shouldRefill && shouldRefillCategory(previous)) {
                    int sourceSlot = findMatchingSlot(inv, previous);
                    if (sourceSlot != -1) {
                        gameMode.handleContainerInput(
                                player.inventoryMenu.containerId,
                                sourceSlot,
                                hotbarSlot,
                                ContainerInput.SWAP,
                                player
                        );
                        // Cooldown: don't check this slot for 5 ticks while server processes
                        refillCooldown[hotbarSlot] = 5;
                    }
                }
            }

            // Update snapshot
            lastHotbar[hotbarSlot] = current.copy();
            lastCount[hotbarSlot] = current.getCount();
        }
    }

    private static void updateSnapshots(Inventory inv) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inv.getItem(i);
            lastHotbar[i] = stack.copy();
            lastCount[i] = stack.getCount();
        }
    }

    private static boolean shouldRefillCategory(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem) return EssentialsConfig.refillBlocks;
        if (stack.has(DataComponents.WEAPON)) return EssentialsConfig.refillWeapons;
        if (stack.has(DataComponents.TOOL)) return EssentialsConfig.refillTools;
        if (stack.has(DataComponents.FOOD)) return EssentialsConfig.refillFood;
        if (stack.has(DataComponents.CONTAINER)) return EssentialsConfig.refillContainers;
        if (stack.has(DataComponents.POTION_CONTENTS)) return EssentialsConfig.refillPotions;
        if (stack.getItem() instanceof ProjectileItem) return EssentialsConfig.refillProjectiles;
        return EssentialsConfig.refillOther;
    }

    private static int findMatchingSlot(Inventory inv, ItemStack target) {
        int bestSlot = -1;
        int bestCount = 0;

        for (int i = 9; i < 36; i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) continue;

            boolean matches;
            if (EssentialsConfig.refillMatchTypeOnly) {
                // Loose: any diamond pickaxe matches any diamond pickaxe
                matches = stack.is(target.getItem());
            } else {
                // Strict: must be identical (same enchantments, same components)
                matches = ItemStack.isSameItemSameComponents(stack, target);
            }

            if (matches && stack.getCount() > bestCount) {
                bestCount = stack.getCount();
                bestSlot = i;
            }
        }
        return bestSlot;
    }

    public static void reset() {
        initialized = false;
        dropKeyRecentTicks = 0;
        for (int i = 0; i < 9; i++) {
            refillCooldown[i] = 0;
        }
    }
}
