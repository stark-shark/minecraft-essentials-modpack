package com.essentials.mixin.inventory;

import com.essentials.config.EssentialsConfig;
import com.essentials.inventory.ContainerActions;
import com.essentials.inventory.InventorySortHandler;
import com.essentials.inventory.MatchHighlighter;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class ContainerSortButtonMixin extends Screen {

    @Shadow protected int leftPos;
    @Shadow protected int topPos;
    @Shadow protected int imageWidth;
    @Shadow protected int imageHeight;
    @Shadow protected abstract <T extends AbstractContainerMenu> T getMenu();

    protected ContainerSortButtonMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void essentials$addButtons(CallbackInfo ci) {
        if (!EssentialsConfig.inventorySortEnabled) return;
        if ((Object) this instanceof CreativeModeInventoryScreen) return;

        boolean isPlayerInv = (Object) this instanceof InventoryScreen;

        if (isPlayerInv) {
            addPlayerInventoryButtons();
        } else {
            addContainerButtons();
        }
    }

    private void addPlayerInventoryButtons() {
        // Player inventory only screen — 3 buttons above main inventory area
        int btnSize = 10;
        int gap = 1;
        int rightEdge = leftPos + imageWidth - 7;
        int btnY = topPos + 84 - btnSize - 2;

        // Sort Inventory
        Button sortBtn = Button.builder(Component.literal("\u2630"), btn ->
                InventorySortHandler.sort(9, 36)
        ).bounds(rightEdge - btnSize, btnY, btnSize, btnSize).build();
        sortBtn.setTooltip(Tooltip.create(Component.literal("Sort Inventory")));
        addRenderableWidget(sortBtn);
    }

    private void addContainerButtons() {
        AbstractContainerMenu menu = getMenu();
        int totalSlots = menu.slots.size();
        int containerSlots = totalSlots - 36;
        if (containerSlots <= 0) return;

        int btnSize = 10;
        int gap = 1;
        int step = btnSize + gap;

        // === Container-side buttons (top-right of container area) ===
        int cRightEdge = leftPos + imageWidth - 7;
        int cBtnY = topPos + 5;

        // Sort Container
        Button sortContBtn = Button.builder(Component.literal("\u2630"), btn ->
                InventorySortHandler.sort(0, containerSlots)
        ).bounds(cRightEdge - btnSize, cBtnY, btnSize, btnSize).build();
        sortContBtn.setTooltip(Tooltip.create(Component.literal("Sort Container")));
        addRenderableWidget(sortContBtn);

        // Move Matching Items (inv -> container)
        Button moveMatchBtn = Button.builder(Component.literal("\u2248"), btn ->
                ContainerActions.moveMatchingToContainer(containerSlots)
        ).bounds(cRightEdge - btnSize - step, cBtnY, btnSize, btnSize).build();
        moveMatchBtn.setTooltip(Tooltip.create(Component.literal("Move Matching Items")));
        addRenderableWidget(moveMatchBtn);

        // Move All (inv -> container)
        Button moveAllBtn = Button.builder(Component.literal("\u2191"), btn ->
                ContainerActions.moveAllToContainer(containerSlots)
        ).bounds(cRightEdge - btnSize - step * 2, cBtnY, btnSize, btnSize).build();
        moveAllBtn.setTooltip(Tooltip.create(Component.literal("Move All")));
        addRenderableWidget(moveAllBtn);

        // Highlight Matching (link icon)
        Button linkBtn = Button.builder(Component.literal("\u26D3"), btn ->
                MatchHighlighter.toggle(containerSlots)
        ).bounds(cRightEdge - btnSize - step * 3, cBtnY, btnSize, btnSize).build();
        linkBtn.setTooltip(Tooltip.create(Component.literal("Highlight Matching Items")));
        addRenderableWidget(linkBtn);

        // === Inventory-side buttons (above player inventory section in container GUI) ===
        // Player inventory area is at the bottom of the container GUI
        // In standard container GUIs, player inv starts ~84px from the bottom
        int invBtnY = topPos + imageHeight - 83 - btnSize - 2;

        // Sort Inventory
        Button sortInvBtn = Button.builder(Component.literal("\u2630"), btn ->
                InventorySortHandler.sort(containerSlots, totalSlots - 9)
        ).bounds(cRightEdge - btnSize, invBtnY, btnSize, btnSize).build();
        sortInvBtn.setTooltip(Tooltip.create(Component.literal("Sort Inventory")));
        addRenderableWidget(sortInvBtn);

        // Take Matching Items (container -> inv)
        Button takeMatchBtn = Button.builder(Component.literal("\u2248"), btn ->
                ContainerActions.takeMatchingFromContainer(containerSlots)
        ).bounds(cRightEdge - btnSize - step, invBtnY, btnSize, btnSize).build();
        takeMatchBtn.setTooltip(Tooltip.create(Component.literal("Take Matching Items")));
        addRenderableWidget(takeMatchBtn);

        // Take All (container -> inv)
        Button takeAllBtn = Button.builder(Component.literal("\u2193"), btn ->
                ContainerActions.takeAllFromContainer(containerSlots)
        ).bounds(cRightEdge - btnSize - step * 2, invBtnY, btnSize, btnSize).build();
        takeAllBtn.setTooltip(Tooltip.create(Component.literal("Take All")));
        addRenderableWidget(takeAllBtn);
    }
}
