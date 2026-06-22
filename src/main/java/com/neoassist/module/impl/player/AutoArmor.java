package com.neoassist.module.impl.player;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.BooleanSetting;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public class AutoArmor extends Module {
    private static final int FIRST_INVENTORY_SLOT = 9;
    private static final int LAST_INVENTORY_SLOT = 44;

    private final BooleanSetting upgrade = new BooleanSetting("Upgrade", "Replace equipped armor with stronger pieces", true);

    private int cooldown;

    public AutoArmor() {
        super("AutoArmor", "Automatically equips the best armor from your inventory", Category.PLAYER);
        addSettings(upgrade);
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.screen != null) {
            return;
        }
        if (cooldown > 0) {
            cooldown--;
            return;
        }
        AbstractContainerMenu menu = player().inventoryMenu;
        for (int i = FIRST_INVENTORY_SLOT; i <= LAST_INVENTORY_SLOT; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (stack.isEmpty()) {
                continue;
            }
            EquipmentSlot slot = player().getEquipmentSlotForItem(stack);
            if (!slot.isArmor()) {
                continue;
            }
            ItemStack equipped = player().getItemBySlot(slot);
            if (equipped.isEmpty()) {
                gameMode().handleInventoryMouseClick(menu.containerId, i, 0, ClickType.QUICK_MOVE, player());
                cooldown = 2;
                return;
            }
            if (upgrade.get() && isBetterArmor(stack, equipped)) {
                int armorSlot = armorSlot(slot);
                if (armorSlot != -1) {
                    gameMode().handleInventoryMouseClick(menu.containerId, i, 0, ClickType.PICKUP, player());
                    gameMode().handleInventoryMouseClick(menu.containerId, armorSlot, 0, ClickType.PICKUP, player());
                    gameMode().handleInventoryMouseClick(menu.containerId, i, 0, ClickType.PICKUP, player());
                    cooldown = 2;
                    return;
                }
            }
        }
    }

    private int armorSlot(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> 5;
            case CHEST -> 6;
            case LEGS -> 7;
            case FEET -> 8;
            default -> -1;
        };
    }

    private boolean isBetterArmor(ItemStack candidate, ItemStack equipped) {
        int candidateValue = armorValue(candidate);
        int equippedValue = armorValue(equipped);
        if (candidateValue != equippedValue) {
            return candidateValue > equippedValue;
        }
        return remainingDurability(candidate) > remainingDurability(equipped);
    }

    private int armorValue(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem armor) {
            return armor.getDefense();
        }
        return 0;
    }

    private int remainingDurability(ItemStack stack) {
        if (!stack.isDamageableItem()) {
            return Integer.MAX_VALUE;
        }
        return stack.getMaxDamage() - stack.getDamageValue();
    }
}
