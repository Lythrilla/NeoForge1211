package com.neoassist.module.impl.combat;

import com.neoassist.module.Category;
import com.neoassist.module.Module;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.Entity;

public class Criticals extends Module {
    public Criticals() {
        super("Criticals", "Always lands critical hits by spoofing micro-jumps", Category.COMBAT);
    }

    @Override
    public void onAttack(Entity target) {
        if (!inGame() || mc.getConnection() == null) {
            return;
        }
        if (!player().onGround() || player().isInWater() || player().isInLava()
                || player().onClimbable() || player().isPassenger()) {
            return;
        }
        double x = player().getX();
        double y = player().getY();
        double z = player().getZ();
        send(x, y + 0.0625, z);
        send(x, y, z);
        send(x, y + 1.1E-5, z);
        send(x, y, z);
    }

    private void send(double x, double y, double z) {
        mc.getConnection().send(new ServerboundMovePlayerPacket.Pos(x, y, z, false));
    }
}
