package com.neoassist.module.impl.movement;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.NumberSetting;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class Step extends Module {
    private final NumberSetting height = new NumberSetting("Height", "Maximum auto-step height", 1.0, 0.6, 3.0, 0.1);

    public Step() {
        super("Step", "Lets you walk up full blocks automatically", Category.MOVEMENT);
        addSettings(height);
    }

    @Override
    public void onTick() {
        if (!inGame()) {
            return;
        }
        AttributeInstance attr = player().getAttribute(Attributes.STEP_HEIGHT);
        if (attr != null) {
            attr.setBaseValue(height.get());
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            AttributeInstance attr = player().getAttribute(Attributes.STEP_HEIGHT);
            if (attr != null) {
                attr.setBaseValue(0.6);
            }
        }
    }
}
