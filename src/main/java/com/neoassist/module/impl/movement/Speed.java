package com.neoassist.module.impl.movement;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.NumberSetting;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class Speed extends Module {
    private static final double DEFAULT_SPEED = 0.1;

    private final NumberSetting speed = new NumberSetting("Speed", "Movement speed multiplier", 1.5, 1.0, 5.0, 0.1);

    public Speed() {
        super("Speed", "Increases your movement speed", Category.MOVEMENT);
        addSettings(speed);
    }

    @Override
    public String getInfo() {
        return String.format("%.1fx", speed.get());
    }

    @Override
    public void onTick() {
        if (!inGame()) {
            return;
        }
        AttributeInstance attr = player().getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null) {
            attr.setBaseValue(DEFAULT_SPEED * speed.get());
        }
    }

    @Override
    public void onDisable() {
        if (mc.player == null) {
            return;
        }
        AttributeInstance attr = player().getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null) {
            attr.setBaseValue(DEFAULT_SPEED);
        }
    }
}
