package com.essentials.tooltip;

import com.essentials.EssentialsMod;
import com.mojang.serialization.DataResult;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class EnderChestCache {

    private static final Path CACHE_DIR = FabricLoader.getInstance().getConfigDir().resolve("essentials").resolve("enderchest");
    private static List<ItemStack> cachedItems = List.of();
    private static boolean hasData = false;
    private static String currentWorldId = null;

    public static void register() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            currentWorldId = getWorldId(client);
            loadFromDisk();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            saveToDisk();
            cachedItems = List.of();
            hasData = false;
            currentWorldId = null;
        });
    }

    public static void capture(NonNullList<ItemStack> items) {
        List<ItemStack> copy = new ArrayList<>(items.size());
        for (ItemStack stack : items) {
            copy.add(stack.copy());
        }
        cachedItems = copy;
        hasData = true;
        saveToDisk();
    }

    public static List<ItemStack> getItems() {
        return cachedItems;
    }

    public static boolean hasData() {
        return hasData;
    }

    private static String getWorldId(Minecraft mc) {
        // Multiplayer: use server address
        if (mc.getCurrentServer() != null) {
            return sanitize(mc.getCurrentServer().ip);
        }
        // Singleplayer: use level name
        if (mc.getSingleplayerServer() != null) {
            return sanitize(mc.getSingleplayerServer().getWorldData().getLevelName());
        }
        return "unknown";
    }

    private static String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private static Path getCacheFile() {
        if (currentWorldId == null) return null;
        return CACHE_DIR.resolve(currentWorldId + ".nbt");
    }

    private static void saveToDisk() {
        if (!hasData || currentWorldId == null) return;

        Path file = getCacheFile();
        if (file == null) return;

        try {
            Files.createDirectories(CACHE_DIR);

            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;

            RegistryAccess registries = mc.level.registryAccess();
            RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);

            CompoundTag root = new CompoundTag();
            ListTag itemList = new ListTag();

            for (int i = 0; i < cachedItems.size(); i++) {
                ItemStack stack = cachedItems.get(i);
                CompoundTag slotTag = new CompoundTag();
                slotTag.putByte("Slot", (byte) i);
                if (!stack.isEmpty()) {
                    DataResult<Tag> result = ItemStack.OPTIONAL_CODEC.encodeStart(ops, stack);
                    result.result().ifPresent(tag -> slotTag.put("Item", tag));
                }
                itemList.add(slotTag);
            }

            root.put("Items", itemList);
            root.putInt("Size", cachedItems.size());
            NbtIo.writeCompressed(root, file);
        } catch (IOException e) {
            EssentialsMod.LOGGER.warn("[Essentials] Failed to save ender chest cache", e);
        }
    }

    private static void loadFromDisk() {
        Path file = getCacheFile();
        if (file == null || !Files.exists(file)) return;

        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;

            RegistryAccess registries = mc.level.registryAccess();
            RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);

            CompoundTag root = NbtIo.readCompressed(file, net.minecraft.nbt.NbtAccounter.unlimitedHeap());
            int size = root.getInt("Size").orElse(27);
            ListTag itemList = root.getList("Items").orElse(new ListTag());

            List<ItemStack> items = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                items.add(ItemStack.EMPTY);
            }

            for (int i = 0; i < itemList.size(); i++) {
                CompoundTag slotTag = itemList.getCompound(i).orElse(new CompoundTag());
                int slot = slotTag.getByte("Slot").orElse((byte) 0) & 0xFF;
                if (slot < size) {
                    Tag itemTag = slotTag.get("Item");
                    if (itemTag != null) {
                        ItemStack.OPTIONAL_CODEC.parse(ops, itemTag)
                                .result()
                                .ifPresent(stack -> items.set(slot, stack));
                    }
                }
            }

            cachedItems = items;
            hasData = true;
            EssentialsMod.LOGGER.info("[Essentials] Loaded ender chest cache for {}", currentWorldId);
        } catch (IOException e) {
            EssentialsMod.LOGGER.warn("[Essentials] Failed to load ender chest cache", e);
        }
    }
}
