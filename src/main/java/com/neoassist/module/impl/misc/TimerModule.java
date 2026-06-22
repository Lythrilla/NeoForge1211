package com.neoassist.module.impl.misc;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.NumberSetting;

public class TimerModule extends Module {
    /** Read by TimerMixin. When inactive, the timer behaves exactly like vanilla. */
    public static volatile boolean active = false;
    public static volatile float multiplier = 1.0F;

    private final NumberSetting speed = new NumberSetting("Speed", "Game speed multiplier", 2.0, 0.1, 10.0, 0.1);

    public TimerModule() {
        super("Timer", "Changes the client tick speed", Category.MISC);
        addSettings(speed);
    }

    @Override
    public void onTick() {
        multiplier = speed.getFloat();
    }

    @Override
    public void onEnable() {
        multiplier = speed.getFloat();
        active = true;
    }

    @Override
    public void onDisable() {
        active = false;
        multiplier = 1.0F;
    }
}
