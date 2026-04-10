package com.essentials.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Set;

public final class MatchHighlighter {

    private static boolean active = false;
    private static final Set<Item> matchingItems = new HashSet<>();

    public static void toggle(int containerSlots) {
        if (active) {
            clear();
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        AbstractContainerMenu menu = player.containerMenu;
        int totalSlots = menu.slots.size();

        // Collect item types in container
        Set<Item> containerItems = new HashSet<>();
        for (int i = 0; i < containerSlots; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (!stack.isEmpty()) {
                containerItems.add(stack.getItem());
            }
        }

        // Collect item types in player inventory (including hotbar)
        Set<Item> playerItems = new HashSet<>();
        for (int i = containerSlots; i < totalSlots; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (!stack.isEmpty()) {
                playerItems.add(stack.getItem());
            }
        }

        // Intersection: items present in BOTH
        matchingItems.clear();
        for (Item item : containerItems) {
            if (playerItems.contains(item)) {
                matchingItems.add(item);
            }
        }

        active = !matchingItems.isEmpty();
    }

    /**
     * Called when a slot's contents change — remove highlight for that item
     * if it no longer exists in both container and inventory.
     */
    public static void onSlotChanged(int containerSlots) {
        if (!active) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        AbstractContainerMenu menu = player.containerMenu;
        int totalSlots = menu.slots.size();

        // Recheck: for each highlighted item, verify it still exists in both sides
        Set<Item> toRemove = new HashSet<>();
        for (Item item : matchingItems) {
            boolean inContainer = false;
            boolean inPlayer = false;

            for (int i = 0; i < containerSlots && !inContainer; i++) {
                if (menu.getSlot(i).getItem().is(item)) inContainer = true;
            }
            for (int i = containerSlots; i < totalSlots && !inPlayer; i++) {
                if (menu.getSlot(i).getItem().is(item)) inPlayer = true;
            }

            if (!inContainer || !inPlayer) {
                toRemove.add(item);
            }
        }

        matchingItems.removeAll(toRemove);
        if (matchingItems.isEmpty()) active = false;
    }

    public static boolean isActive() {
        return active;
    }

    public static boolean shouldHighlight(Slot slot) {
        if (!active) return false;
        ItemStack stack = slot.getItem();
        return !stack.isEmpty() && matchingItems.contains(stack.getItem());
    }

    public static void clear() {
        active = false;
        matchingItems.clear();
    }
}
