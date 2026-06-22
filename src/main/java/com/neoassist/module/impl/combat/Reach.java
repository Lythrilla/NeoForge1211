package com.neoassist.module.impl.combat;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.NumberSetting;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class Reach extends Module {
    private static final double DEFAULT_ENTITY_RANGE = 3.0;

    private final NumberSetting range = new NumberSetting("Range", "Entity interaction range", 4.5, 3.0, 6.0, 0.1);

    public Reach() {
        super("Reach", "Extends your attack/interaction range", Category.COMBAT);
        addSettings(range);
    }

    @Override
    public void onTick() {
        if (!inGame()) {
            return;
        }
        AttributeInstance attr = player().getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (attr != null) {
            attr.setBaseValue(range.get());
        }
    }

    @Override
    public void onDisable() {
        if (mc.player == null) {
            return;
        }
        AttributeInstance attr = player().getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (attr != null) {
            attr.setBaseValue(DEFAULT_ENTITY_RANGE);
        }
    }
}
