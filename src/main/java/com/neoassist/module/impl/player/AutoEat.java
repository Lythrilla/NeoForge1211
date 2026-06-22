package com.neoassist.module.impl.player;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.NumberSetting;
import com.neoassist.util.InventoryUtil;

public class AutoEat extends Module {
    private final NumberSetting hunger = new NumberSetting("Hunger", "Start eating at or below this food level", 16, 1, 19, 1);

    private boolean eating;
    private int previousSlot = -1;

    public AutoEat() {
        super("AutoEat", "Automatically eats food when hungry", Category.PLAYER);
        addSettings(hunger);
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

        int foodLevel = player().getFoodData().getFoodLevel();

        if (eating) {
            // keep eating until full or out of food
            if (foodLevel >= 19 || !InventoryUtil.isEdible(player().getInventory().getItem(player().getInventory().selected))) {
                stop();
            } else {
                mc.options.keyUse.setDown(true);
            }
            return;
        }

        if (foodLevel <= hunger.getInt()) {
            int slot = InventoryUtil.findFoodHotbarSlot(player());
            if (slot != -1) {
                previousSlot = player().getInventory().selected;
                player().getInventory().selected = slot;
                eating = true;
                mc.options.keyUse.setDown(true);
            }
        }
    }

    private void stop() {
        if (eating) {
            mc.options.keyUse.setDown(false);
            if (previousSlot != -1) {
                player().getInventory().selected = previousSlot;
            }
        }
        eating = false;
        previousSlot = -1;
    }

    @Override
    public String getInfo() {
        return eating ? "eating" : null;
    }
}
