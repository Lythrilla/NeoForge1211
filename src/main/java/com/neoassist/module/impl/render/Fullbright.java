package com.neoassist.module.impl.render;

import com.neoassist.module.Category;
import com.neoassist.module.Module;

public class Fullbright extends Module {
    private double previousGamma = 1.0;

    public Fullbright() {
        super("Fullbright", "Maximizes screen brightness", Category.RENDER);
    }

    @Override
    public void onEnable() {
        previousGamma = mc.options.gamma().get();
    }

    @Override
    public void onTick() {
        mc.options.gamma().set(1.0);
    }

    @Override
    public void onDisable() {
        mc.options.gamma().set(previousGamma);
    }
}
