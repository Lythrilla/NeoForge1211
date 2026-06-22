package com.neoassist.module.impl.combat;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.NumberSetting;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class Reach extends Module {
    private static final double DEFAULT_ENTITY_RANGE = 3.0;
    private static final double DEFAULT_BLOCK_RANGE = 4.5;

    private final NumberSetting range = new NumberSetting("Range", "Entity interaction range", 4.5, 3.0, 6.0, 0.1);
    private final NumberSetting blockRange = new NumberSetting("BlockRange", "Block interaction range", 5.0, 4.5, 8.0, 0.1);

    public Reach() {
        super("Reach", "Extends your attack/interaction range", Category.COMBAT);
        addSettings(range, blockRange);
    }

    @Override
    public void onTick() {
        if (!inGame()) {
            return;
        }
        AttributeInstance entityAttr = player().getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (entityAttr != null) {
            entityAttr.setBaseValue(range.get());
        }
        AttributeInstance blockAttr = player().getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        if (blockAttr != null) {
            blockAttr.setBaseValue(blockRange.get());
        }
    }

    @Override
    public void onDisable() {
        if (mc.player == null) {
            return;
        }
        AttributeInstance entityAttr = player().getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (entityAttr != null) {
            entityAttr.setBaseValue(DEFAULT_ENTITY_RANGE);
        }
        AttributeInstance blockAttr = player().getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        if (blockAttr != null) {
            blockAttr.setBaseValue(DEFAULT_BLOCK_RANGE);
        }
    }
}
