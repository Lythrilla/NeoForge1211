package com.neoassist.module.impl.misc;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.NumberSetting;
import com.neoassist.util.LagHelper;

public class FakeLag extends Module {
    /** Read by ConnectionMixin. When inactive, no packets are ever held. */
    public static volatile boolean active = false;

    private final NumberSetting delay = new NumberSetting("Delay", "Ticks to hold movement packets", 10, 1, 60, 1);

    private int ticks;

    public FakeLag() {
        super("FakeLag", "Briefly holds outgoing movement packets, then bursts them (blink)", Category.MISC);
        addSettings(delay);
    }

    @Override
    public void onTick() {
        if (++ticks >= delay.getInt()) {
            ticks = 0;
            LagHelper.flush();
        }
    }

    @Override
    public void onEnable() {
        ticks = 0;
        active = true;
    }

    @Override
    public void onDisable() {
        active = false;
        LagHelper.flush();
    }
}
