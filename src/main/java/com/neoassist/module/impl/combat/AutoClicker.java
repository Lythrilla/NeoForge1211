package com.neoassist.module.impl.combat;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.ModeSetting;
import com.neoassist.module.setting.NumberSetting;

import net.minecraft.client.KeyMapping;

public class AutoClicker extends Module {
    private final ModeSetting button = new ModeSetting("Button", "Which mouse button to spam", "Left", "Left", "Right");
    private final NumberSetting cps = new NumberSetting("CPS", "Clicks per second", 10, 1, 20, 1);

    private long lastClick;

    public AutoClicker() {
        super("AutoClicker", "Automatically clicks at a fixed rate", Category.COMBAT);
        addSettings(button, cps);
    }

    @Override
    public String getInfo() {
        return button.get();
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.screen != null) {
            return;
        }
        long now = System.currentTimeMillis();
        long interval = (long) (1000.0 / cps.get());
        if (now - lastClick < interval) {
            return;
        }
        lastClick = now;
        KeyMapping key = button.is("Left") ? mc.options.keyAttack : mc.options.keyUse;
        KeyMapping.click(key.getKey());
    }
}
