package com.neoassist.module.impl.combat;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.BooleanSetting;
import com.neoassist.module.setting.NumberSetting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AutoGapple extends Module {
    private final NumberSetting health = new NumberSetting("Health", "Eat at or below this health", 10, 1, 19, 1);
    private final BooleanSetting preferEnchanted = new BooleanSetting("PreferEnchanted", "Prefer enchanted golden apple", true);

    private boolean eating;
    private int previousSlot = -1;

    public AutoGapple() {
        super("AutoGapple", "Automatically eats golden apples when low health", Category.COMBAT);
        addSettings(health, preferEnchanted);
    }

    @Override
    public String getInfo() {
        return eating ? "eating" : null;
    }

    @Override
    public void onDisable() {
        stop();
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.screen != null) {
            if (eating) {
                stop();
            }
            return;
        }

        float hp = player().getHealth() + player().getAbsorptionAmount();

        if (eating) {
            ItemStack held = player().getInventory().getItem(player().getInventory().selected);
            if (hp > health.get() + 4 || !isGapple(held)) {
                stop();
            } else {
                mc.options.keyUse.setDown(true);
            }
            return;
        }

        if (hp <= health.get()) {
            int slot = findGappleSlot();
            if (slot != -1) {
                previousSlot = player().getInventory().selected;
                player().getInventory().selected = slot;
                eating = true;
                mc.options.keyUse.setDown(true);
            }
        }
    }

    private int findGappleSlot() {
        int enchSlot = -1;
        int normalSlot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player().getInventory().getItem(i);
            if (stack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
                enchSlot = i;
            } else if (stack.is(Items.GOLDEN_APPLE)) {
                normalSlot = i;
            }
        }
        if (preferEnchanted.get() && enchSlot != -1) {
            return enchSlot;
        }
        return enchSlot != -1 ? enchSlot : normalSlot;
    }

    private boolean isGapple(ItemStack stack) {
        return stack.is(Items.GOLDEN_APPLE) || stack.is(Items.ENCHANTED_GOLDEN_APPLE);
    }

    private void stop() {
        if (eating) {
            mc.options.keyUse.setDown(false);
            if (previousSlot != -1 && mc.player != null) {
                player().getInventory().selected = previousSlot;
            }
        }
        eating = false;
        previousSlot = -1;
    }
}
