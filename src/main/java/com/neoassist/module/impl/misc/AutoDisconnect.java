package com.neoassist.module.impl.misc;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.NumberSetting;

import net.minecraft.network.chat.Component;

public class AutoDisconnect extends Module {
    private final NumberSetting health = new NumberSetting("Health", "Disconnect at or below this health", 4, 1, 19, 1);

    public AutoDisconnect() {
        super("AutoDisconnect", "Disconnects when your health drops too low", Category.MISC);
        addSettings(health);
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.getConnection() == null) {
            return;
        }
        float hp = player().getHealth() + player().getAbsorptionAmount();
        if (hp <= health.get() && player().isAlive()) {
            mc.getConnection().getConnection().disconnect(
                    Component.literal("AutoDisconnect: health dropped to " + String.format("%.1f", hp)));
            setEnabled(false);
        }
    }

    @Override
    public String getInfo() {
        return "\u2264" + health.getInt();
    }
}
