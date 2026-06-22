package com.neoassist.module.impl.combat;

import com.neoassist.module.Category;
import com.neoassist.module.Module;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Items;

public class AutoTotem extends Module {
    private static final int OFFHAND_SLOT = 45;

    public AutoTotem() {
        super("AutoTotem", "Keeps a Totem of Undying in your off-hand", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.screen != null) {
            return;
        }
        AbstractContainerMenu menu = player().inventoryMenu;
        if (menu.getSlot(OFFHAND_SLOT).getItem().is(Items.TOTEM_OF_UNDYING)) {
            return;
        }
        int totemSlot = -1;
        for (int i = 0; i < menu.slots.size(); i++) {
            if (i == OFFHAND_SLOT) {
                continue;
            }
            if (menu.getSlot(i).getItem().is(Items.TOTEM_OF_UNDYING)) {
                totemSlot = i;
                break;
            }
        }
        if (totemSlot == -1) {
            return;
        }
        int id = menu.containerId;
        gameMode().handleInventoryMouseClick(id, totemSlot, 0, ClickType.PICKUP, player());
        gameMode().handleInventoryMouseClick(id, OFFHAND_SLOT, 0, ClickType.PICKUP, player());
        gameMode().handleInventoryMouseClick(id, totemSlot, 0, ClickType.PICKUP, player());
    }
}
