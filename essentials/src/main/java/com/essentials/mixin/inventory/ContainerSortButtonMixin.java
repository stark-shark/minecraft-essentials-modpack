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
import net.minecraft.world.inventory.MenuType;
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
        if ((Object) this instanceof InventoryScreen) return;

        if (!isStorageContainer()) return;
        addContainerButtons();
    }

    private boolean isStorageContainer() {
        try {
            MenuType<?> type = getMenu().getType();
            return type == MenuType.GENERIC_9x1
                    || type == MenuType.GENERIC_9x2
                    || type == MenuType.GENERIC_9x3
                    || type == MenuType.GENERIC_9x4
                    || type == MenuType.GENERIC_9x5
                    || type == MenuType.GENERIC_9x6
                    || type == MenuType.GENERIC_3x3
                    || type == MenuType.HOPPER
                    || type == MenuType.SHULKER_BOX;
        } catch (UnsupportedOperationException e) {
            return false;
        }
    }

    private void addContainerButtons() {
        AbstractContainerMenu menu = getMenu();
        int totalSlots = menu.slots.size();
        int containerSlots = totalSlots - 36;
        if (containerSlots <= 0) return;

        int btnSize = 10;
        int gap = 1;
        int step = btnSize + gap;
        int cRightEdge = leftPos + imageWidth - 7;
        int cBtnY = topPos + 5;

        Button sortContBtn = Button.builder(Component.literal("\u2630"), btn ->
                InventorySortHandler.sort(0, containerSlots)
        ).bounds(cRightEdge - btnSize, cBtnY, btnSize, btnSize).build();
        sortContBtn.setTooltip(Tooltip.create(Component.literal("Sort Container")));
        addRenderableWidget(sortContBtn);

        Button moveMatchBtn = Button.builder(Component.literal("\u2248"), btn ->
                ContainerActions.moveMatchingToContainer(containerSlots)
        ).bounds(cRightEdge - btnSize - step, cBtnY, btnSize, btnSize).build();
        moveMatchBtn.setTooltip(Tooltip.create(Component.literal("Move Matching Items")));
        addRenderableWidget(moveMatchBtn);

        Button moveAllBtn = Button.builder(Component.literal("\u2191"), btn ->
                ContainerActions.moveAllToContainer(containerSlots)
        ).bounds(cRightEdge - btnSize - step * 2, cBtnY, btnSize, btnSize).build();
        moveAllBtn.setTooltip(Tooltip.create(Component.literal("Move All")));
        addRenderableWidget(moveAllBtn);

        Button linkBtn = Button.builder(Component.literal("\u26D3"), btn ->
                MatchHighlighter.toggle(containerSlots)
        ).bounds(cRightEdge - btnSize - step * 3, cBtnY, btnSize, btnSize).build();
        linkBtn.setTooltip(Tooltip.create(Component.literal("Highlight Matching Items")));
        addRenderableWidget(linkBtn);

        // === Inventory-side buttons ===
        int invBtnY = topPos + imageHeight - 83 - btnSize - 2;

        Button sortInvBtn = Button.builder(Component.literal("\u2630"), btn ->
                InventorySortHandler.sort(containerSlots, totalSlots - 9)
        ).bounds(cRightEdge - btnSize, invBtnY, btnSize, btnSize).build();
        sortInvBtn.setTooltip(Tooltip.create(Component.literal("Sort Inventory")));
        addRenderableWidget(sortInvBtn);

        Button takeMatchBtn = Button.builder(Component.literal("\u2248"), btn ->
                ContainerActions.takeMatchingFromContainer(containerSlots)
        ).bounds(cRightEdge - btnSize - step, invBtnY, btnSize, btnSize).build();
        takeMatchBtn.setTooltip(Tooltip.create(Component.literal("Take Matching Items")));
        addRenderableWidget(takeMatchBtn);

        Button takeAllBtn = Button.builder(Component.literal("\u2193"), btn ->
                ContainerActions.takeAllFromContainer(containerSlots)
        ).bounds(cRightEdge - btnSize - step * 2, invBtnY, btnSize, btnSize).build();
        takeAllBtn.setTooltip(Tooltip.create(Component.literal("Take All")));
        addRenderableWidget(takeAllBtn);
    }
}
