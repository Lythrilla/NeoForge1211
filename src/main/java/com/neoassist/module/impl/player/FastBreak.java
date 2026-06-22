package com.neoassist.module.impl.player;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.NumberSetting;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class FastBreak extends Module {
    private static final double DEFAULT_EFFICIENCY = 1.0;

    private final NumberSetting speed = new NumberSetting("Speed", "Mining speed multiplier", 1.5, 1.0, 5.0, 0.1);

    public FastBreak() {
        super("FastBreak", "Speeds up block breaking", Category.PLAYER);
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
        AttributeInstance attr = player().getAttribute(Attributes.BLOCK_BREAK_SPEED);
        if (attr != null) {
            attr.setBaseValue(speed.get());
        }
    }

    @Override
    public void onDisable() {
        if (mc.player == null) {
            return;
        }
        AttributeInstance attr = player().getAttribute(Attributes.BLOCK_BREAK_SPEED);
        if (attr != null) {
            attr.setBaseValue(DEFAULT_EFFICIENCY);
        }
    }
}
