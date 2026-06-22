package com.neoassist.module.impl.player;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.BooleanSetting;
import com.neoassist.module.setting.NumberSetting;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

public class ChestStealer extends Module {
    private final NumberSetting delay = new NumberSetting("Delay", "Ticks between each transferred stack", 1, 0, 10, 1);
    private final BooleanSetting closeWhenDone = new BooleanSetting("CloseWhenDone", "Close the container after looting it", false);

    private int ticks;

    public ChestStealer() {
        super("ChestStealer", "Quickly moves all items from an open container", Category.PLAYER);
        addSettings(delay, closeWhenDone);
    }

    @Override
    public void onTick() {
        if (!inGame()) {
            return;
        }
        if (!(mc.screen instanceof AbstractContainerScreen<?> screen)
                || mc.screen instanceof InventoryScreen
                || mc.screen instanceof CreativeModeInventoryScreen) {
            return;
        }
        if (ticks > 0) {
            ticks--;
            return;
        }

        AbstractContainerMenu menu = screen.getMenu();
        for (int slotId = 0; slotId < menu.slots.size(); slotId++) {
            Slot slot = menu.getSlot(slotId);
            if (slot.container != player().getInventory() && slot.hasItem()) {
                gameMode().handleInventoryMouseClick(menu.containerId, slotId, 0, ClickType.QUICK_MOVE, player());
                ticks = delay.getInt();
                return;
            }
        }
        if (closeWhenDone.get()) {
            player().closeContainer();
        }
    }
}
