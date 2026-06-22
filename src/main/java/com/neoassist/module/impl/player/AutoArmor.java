package com.neoassist.module.impl.player;

import com.neoassist.module.Category;
import com.neoassist.module.Module;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

public class AutoArmor extends Module {
    public AutoArmor() {
        super("AutoArmor", "Automatically equips armor into empty slots", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.screen != null) {
            return;
        }
        AbstractContainerMenu menu = player().inventoryMenu;
        // main inventory + hotbar slots in the player inventory menu are indices 9..44
        for (int i = 9; i <= 44; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (stack.isEmpty()) {
                continue;
            }
            EquipmentSlot slot = player().getEquipmentSlotForItem(stack);
            if (!slot.isArmor()) {
                continue;
            }
            if (player().getItemBySlot(slot).isEmpty()) {
                gameMode().handleInventoryMouseClick(menu.containerId, i, 0, ClickType.QUICK_MOVE, player());
                return; // one piece per tick to avoid desync
            }
        }
    }
}
