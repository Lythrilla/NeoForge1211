package com.neoassist.module.impl.misc;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.NumberSetting;

import net.minecraft.world.InteractionHand;

public class AntiAFK extends Module {
    private final NumberSetting interval = new NumberSetting("Interval", "Seconds between actions", 20, 5, 120, 1);

    private int ticks;

    public AntiAFK() {
        super("AntiAFK", "Performs small actions to avoid AFK kicks", Category.MISC);
        addSettings(interval);
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.screen != null) {
            return;
        }
        if (ticks++ < interval.getInt() * 20) {
            return;
        }
        ticks = 0;
        player().setYRot(player().getYRot() + 18.0F);
        player().swing(InteractionHand.MAIN_HAND);
    }
}
