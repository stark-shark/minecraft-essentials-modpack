package com.essentials.inventory;

import com.essentials.config.EssentialsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class InventorySortHandler {

    private static final int CAT_WEAPON = 0;
    private static final int CAT_TOOL = 1;
    private static final int CAT_ARMOR = 2;
    private static final int CAT_BLOCK = 3;
    private static final int CAT_FOOD = 4;
    private static final int CAT_MATERIAL = 5;
    private static final int CAT_OTHER = 6;

    /**
     * Sort a range of slots in the currently active container.
     * @param slotStart first slot index (inclusive)
     * @param slotEnd last slot index (exclusive)
     */
    public static void sort(int slotStart, int slotEnd) {
        if (!EssentialsConfig.inventorySortEnabled) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        MultiPlayerGameMode gameMode = mc.gameMode;
        if (player == null || gameMode == null) return;

        AbstractContainerMenu menu = player.containerMenu;
        int containerId = menu.containerId;

        List<SlotEntry> entries = new ArrayList<>();
        for (int i = slotStart; i < slotEnd; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            entries.add(new SlotEntry(i, stack.copy()));
        }

        List<SlotEntry> sorted = new ArrayList<>(entries);
        sorted.sort(Comparator
                .comparingInt((SlotEntry e) -> getCategory(e.stack))
                .thenComparing(e -> getItemName(e.stack))
                .thenComparingInt(e -> -e.stack.getCount())
        );

        for (int targetIdx = 0; targetIdx < sorted.size(); targetIdx++) {
            SlotEntry desired = sorted.get(targetIdx);

            int currentIdx = -1;
            for (int j = targetIdx; j < entries.size(); j++) {
                if (entries.get(j).slot == desired.slot) {
                    currentIdx = j;
                    break;
                }
            }

            if (currentIdx == -1 || currentIdx == targetIdx) continue;

            int slotA = entries.get(targetIdx).slot;
            int slotB = entries.get(currentIdx).slot;

            // Swap via pickup: pick up A, click B (swaps), place back at A
            gameMode.handleContainerInput(containerId, slotA, 0, ContainerInput.PICKUP, player);
            gameMode.handleContainerInput(containerId, slotB, 0, ContainerInput.PICKUP, player);
            gameMode.handleContainerInput(containerId, slotA, 0, ContainerInput.PICKUP, player);

            // Update tracking
            SlotEntry temp = entries.get(targetIdx);
            entries.set(targetIdx, entries.get(currentIdx));
            entries.set(currentIdx, temp);
            int tmpSlot = entries.get(targetIdx).slot;
            entries.get(targetIdx).slot = entries.get(currentIdx).slot;
            entries.get(currentIdx).slot = tmpSlot;
        }
    }

    private static int getCategory(ItemStack stack) {
        if (stack.isEmpty()) return CAT_OTHER + 1;
        if (stack.has(DataComponents.WEAPON)) return CAT_WEAPON;
        if (stack.has(DataComponents.TOOL)) return CAT_TOOL;
        if (stack.has(DataComponents.EQUIPPABLE)) return CAT_ARMOR;
        if (stack.getItem() instanceof BlockItem) return CAT_BLOCK;
        if (stack.has(DataComponents.FOOD)) return CAT_FOOD;
        if (stack.getMaxStackSize() > 1 && !stack.has(DataComponents.CONSUMABLE)) return CAT_MATERIAL;
        return CAT_OTHER;
    }

    private static String getItemName(ItemStack stack) {
        if (stack.isEmpty()) return "zzz";
        return stack.getHoverName().getString().toLowerCase();
    }

    private static class SlotEntry {
        int slot;
        final ItemStack stack;
        SlotEntry(int slot, ItemStack stack) {
            this.slot = slot;
            this.stack = stack;
        }
    }
}
