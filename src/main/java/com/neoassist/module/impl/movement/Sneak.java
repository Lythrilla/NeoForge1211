package com.neoassist.module.impl.movement;

import com.neoassist.module.Category;
import com.neoassist.module.Module;

public class Sneak extends Module {
    public Sneak() {
        super("Sneak", "Automatically sneaks", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (inGame() && mc.screen == null) {
            mc.options.keyShift.setDown(true);
        }
    }

    @Override
    public void onDisable() {
        mc.options.keyShift.setDown(false);
    }
}
