package com.essentials.mixin.inventory;

import com.essentials.inventory.MatchHighlighter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class ContainerHighlightMixin {

    @Shadow protected int leftPos;
    @Shadow protected int topPos;
    @Shadow protected abstract <T extends AbstractContainerMenu> T getMenu();

    @Inject(method = "extractSlots", at = @At("TAIL"))
    private void essentials$drawMatchHighlights(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (!MatchHighlighter.isActive()) return;

        AbstractContainerMenu menu = getMenu();
        for (Slot slot : menu.slots) {
            if (MatchHighlighter.shouldHighlight(slot)) {
                // extractSlots runs in container-local coordinates — slot.x/y are already correct
                int x = slot.x;
                int y = slot.y;
                int border = 0xFFFF3333; // Red

                // Draw 1px red border around the 16x16 slot
                graphics.fill(x - 1, y - 1, x + 17, y, border);      // Top
                graphics.fill(x - 1, y + 16, x + 17, y + 17, border); // Bottom
                graphics.fill(x - 1, y, x, y + 16, border);           // Left
                graphics.fill(x + 16, y, x + 17, y + 16, border);     // Right
            }
        }
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void essentials$clearHighlightsOnClose(CallbackInfo ci) {
        MatchHighlighter.clear();
    }
}
