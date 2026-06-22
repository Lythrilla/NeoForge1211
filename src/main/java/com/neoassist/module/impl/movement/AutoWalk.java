package com.neoassist.module.impl.movement;

import com.neoassist.module.Category;
import com.neoassist.module.Module;

public class AutoWalk extends Module {
    public AutoWalk() {
        super("AutoWalk", "Automatically walks forward", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        mc.options.keyUp.setDown(inGame() && mc.screen == null);
    }

    @Override
    public void onDisable() {
        mc.options.keyUp.setDown(false);
    }
}
