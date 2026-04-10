package com.essentials.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Set;

public final class ContainerActions {

    /**
     * Move all items from player main inventory (not hotbar) into the container.
     */
    public static void moveAllToContainer(int containerSlots) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        MultiPlayerGameMode gameMode = mc.gameMode;
        if (player == null || gameMode == null) return;

        AbstractContainerMenu menu = player.containerMenu;
        int containerId = menu.containerId;
        int totalSlots = menu.slots.size();
        int playerInvStart = containerSlots;
        int playerInvEnd = totalSlots - 9;

        for (int i = playerInvStart; i < playerInvEnd; i++) {
            if (!menu.getSlot(i).getItem().isEmpty()) {
                gameMode.handleContainerInput(containerId, i, 0, ContainerInput.QUICK_MOVE, player);
            }
        }
    }

    /**
     * Move only items that match what's already in the container from player inv.
     */
    public static void moveMatchingToContainer(int containerSlots) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        MultiPlayerGameMode gameMode = mc.gameMode;
        if (player == null || gameMode == null) return;

        AbstractContainerMenu menu = player.containerMenu;
        int containerId = menu.containerId;
        int totalSlots = menu.slots.size();

        Set<String> containerItemKeys = collectItemKeys(menu, 0, containerSlots);
        if (containerItemKeys.isEmpty()) return;

        int playerInvStart = containerSlots;
        int playerInvEnd = totalSlots - 9;

        for (int i = playerInvStart; i < playerInvEnd; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (!stack.isEmpty() && containerItemKeys.contains(getItemKey(stack))) {
                gameMode.handleContainerInput(containerId, i, 0, ContainerInput.QUICK_MOVE, player);
            }
        }
    }

    /**
     * Take all items from the container into player inventory.
     */
    public static void takeAllFromContainer(int containerSlots) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        MultiPlayerGameMode gameMode = mc.gameMode;
        if (player == null || gameMode == null) return;

        AbstractContainerMenu menu = player.containerMenu;
        int containerId = menu.containerId;

        for (int i = 0; i < containerSlots; i++) {
            if (!menu.getSlot(i).getItem().isEmpty()) {
                gameMode.handleContainerInput(containerId, i, 0, ContainerInput.QUICK_MOVE, player);
            }
        }
    }

    /**
     * Take only items from the container that match what's in the player's inventory.
     */
    public static void takeMatchingFromContainer(int containerSlots) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        MultiPlayerGameMode gameMode = mc.gameMode;
        if (player == null || gameMode == null) return;

        AbstractContainerMenu menu = player.containerMenu;
        int containerId = menu.containerId;
        int totalSlots = menu.slots.size();

        // Collect item types in player's main inventory (not hotbar)
        int playerInvStart = containerSlots;
        int playerInvEnd = totalSlots - 9;
        Set<String> playerItemKeys = collectItemKeys(menu, playerInvStart, playerInvEnd);
        if (playerItemKeys.isEmpty()) return;

        for (int i = 0; i < containerSlots; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (!stack.isEmpty() && playerItemKeys.contains(getItemKey(stack))) {
                gameMode.handleContainerInput(containerId, i, 0, ContainerInput.QUICK_MOVE, player);
            }
        }
    }

    private static Set<String> collectItemKeys(AbstractContainerMenu menu, int from, int to) {
        Set<String> keys = new HashSet<>();
        for (int i = from; i < to; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (!stack.isEmpty()) {
                keys.add(getItemKey(stack));
            }
        }
        return keys;
    }

    private static String getItemKey(ItemStack stack) {
        return stack.getItem().builtInRegistryHolder().key().identifier().toString();
    }
}
