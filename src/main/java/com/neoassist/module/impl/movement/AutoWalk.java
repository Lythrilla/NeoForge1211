package com.neoassist.module.impl.movement;

import com.neoassist.module.Category;
import com.neoassist.module.Module;

public class AutoWalk extends Module {
    public AutoWalk() {
        super("AutoWalk", "Automatically walks forward", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (inGame() && mc.screen == null) {
            mc.options.keyUp.setDown(true);
        }
    }

    @Override
    public void onDisable() {
        mc.options.keyUp.setDown(false);
    }
}
