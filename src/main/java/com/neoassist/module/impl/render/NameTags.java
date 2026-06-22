package com.neoassist.module.impl.render;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.NumberSetting;

public class NameTags extends Module {
    /** Read by EntityRendererMixin. When inactive, name tags render at vanilla scale. */
    public static volatile boolean active = false;
    public static volatile float scale = 2.0F;

    private final NumberSetting size = new NumberSetting("Scale", "Name tag size multiplier", 2.0, 1.0, 5.0, 0.1);

    public NameTags() {
        super("NameTags", "Enlarges entity name tags for readability", Category.RENDER);
        addSettings(size);
    }

    @Override
    public void onTick() {
        scale = size.getFloat();
    }

    @Override
    public void onEnable() {
        scale = size.getFloat();
        active = true;
    }

    @Override
    public void onDisable() {
        active = false;
    }
}
