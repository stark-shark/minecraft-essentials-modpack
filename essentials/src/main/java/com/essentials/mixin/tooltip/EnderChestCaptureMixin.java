package com.essentials.mixin.tooltip;

import com.essentials.config.EssentialsConfig;
import com.essentials.tooltip.EnderChestCache;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Captures ender chest contents when the screen closes.
 * Uses screen title instead of PlayerEnderChestContainer because
 * startOpen/stopOpen are server-side only and never fire on the client
 * for multiplayer/realms.
 */
@Mixin(AbstractContainerScreen.class)
public abstract class EnderChestCaptureMixin extends Screen {

    @Shadow protected abstract <T extends AbstractContainerMenu> T getMenu();

    protected EnderChestCaptureMixin() { super(null); }

    @Inject(method = "removed", at = @At("HEAD"))
    private void essentials$captureEnderChestOnClose(CallbackInfo ci) {
        if (!EssentialsConfig.enderChestTooltipEnabled) return;

        // Check screen title — "Ender Chest" works on any server/realm/language
        String title = this.getTitle().getString();
        if (!title.equals("Ender Chest")) return;

        AbstractContainerMenu menu = getMenu();
        int totalSlots = menu.slots.size();
        int containerSlots = totalSlots - 36;
        if (containerSlots != 27) return;

        NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
        for (int i = 0; i < 27; i++) {
            items.set(i, menu.getSlot(i).getItem().copy());
        }
        EnderChestCache.capture(items);
    }
}
