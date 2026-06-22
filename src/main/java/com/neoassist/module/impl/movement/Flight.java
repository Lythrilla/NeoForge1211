package com.neoassist.module.impl.movement;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.NumberSetting;

public class Flight extends Module {
    private final NumberSetting speed = new NumberSetting("Speed", "Flight speed multiplier", 1.0, 0.5, 5.0, 0.1);

    public Flight() {
        super("Flight", "Creative-style flight (server may restrict)", Category.MOVEMENT);
        addSettings(speed);
    }

    @Override
    public void onEnable() {
        if (mc.player != null) {
            player().getAbilities().mayfly = true;
            player().onUpdateAbilities();
        }
    }

    @Override
    public void onTick() {
        if (!inGame()) {
            return;
        }
        player().getAbilities().mayfly = true;
        player().getAbilities().setFlyingSpeed((float) (0.05 * speed.get()));
    }

    @Override
    public void onDisable() {
        if (mc.player == null) {
            return;
        }
        boolean creative = player().isCreative();
        player().getAbilities().flying = false;
        if (!creative) {
            player().getAbilities().mayfly = false;
        }
        player().getAbilities().setFlyingSpeed(0.05F);
        player().onUpdateAbilities();
    }
}
