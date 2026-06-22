package com.neoassist.module.impl.render;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.NumberSetting;

import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;

public class Zoom extends Module {
    private final NumberSetting factor = new NumberSetting("Factor", "Zoom magnification", 3.0, 1.5, 8.0, 0.5);

    public Zoom() {
        super("Zoom", "Toggle to zoom in (bind a key for quick access)", Category.RENDER);
        addSettings(factor);
    }

    @Override
    public void onFov(ComputeFovModifierEvent event) {
        event.setNewFovModifier(event.getNewFovModifier() / factor.getFloat());
    }
}
