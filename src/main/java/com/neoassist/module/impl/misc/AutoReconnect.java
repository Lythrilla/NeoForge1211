package com.neoassist.module.impl.misc;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.NumberSetting;

import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;

public class AutoReconnect extends Module {
    private final NumberSetting delay = new NumberSetting("Delay", "Seconds before reconnecting", 5, 1, 30, 1);

    private int ticks;

    public AutoReconnect() {
        super("AutoReconnect", "Automatically rejoins the last server after a disconnect", Category.MISC);
        addSettings(delay);
    }

    @Override
    public void onTickAlways() {
        if (mc.player != null || !(mc.screen instanceof DisconnectedScreen)) {
            ticks = 0;
            return;
        }
        ServerData server = mc.getCurrentServer();
        if (server == null) {
            return;
        }
        if (++ticks >= delay.getInt() * 20) {
            ticks = 0;
            ConnectScreen.startConnecting(new TitleScreen(), mc,
                    ServerAddress.parseString(server.ip), server, false, null);
        }
    }
}
