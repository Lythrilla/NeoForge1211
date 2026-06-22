package com.neoassist.module.impl.player;

import com.neoassist.module.Category;
import com.neoassist.module.Module;

import net.minecraft.client.gui.screens.DeathScreen;

public class AutoRespawn extends Module {
    public AutoRespawn() {
        super("AutoRespawn", "Instantly respawns after you die", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (mc.player != null && mc.screen instanceof DeathScreen) {
            mc.player.respawn();
            mc.setScreen(null);
        }
    }
}
