package com.neoassist.module.impl.player;

import com.neoassist.module.Category;
import com.neoassist.module.Module;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

public class NoFall extends Module {
    public NoFall() {
        super("NoFall", "Prevents fall damage by spoofing ground state", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.getConnection() == null) {
            return;
        }
        if (!player().onGround() && player().fallDistance > 2.0F && player().getDeltaMovement().y < 0) {
            mc.getConnection().send(new ServerboundMovePlayerPacket.StatusOnly(true));
        }
    }
}
