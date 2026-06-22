package com.neoassist.module.impl.combat;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.ModeSetting;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.Entity;

public class Criticals extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Critical hit method",
            "Packet", "Packet", "MiniJump");

    public Criticals() {
        super("Criticals", "Always lands critical hits", Category.COMBAT);
        addSettings(mode);
    }

    @Override
    public String getInfo() {
        return mode.get();
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
        if (mode.is("Packet")) {
            attackPacket();
        } else if (mode.is("MiniJump")) {
            attackMiniJump();
        }
    }

    private void attackPacket() {
        double x = player().getX();
        double y = player().getY();
        double z = player().getZ();
        send(x, y + 0.0625, z);
        send(x, y, z);
        send(x, y + 1.1E-5, z);
        send(x, y, z);
    }

    private void attackMiniJump() {
        player().jumpFromGround();
        player().setDeltaMovement(player().getDeltaMovement().multiply(1, 0.25, 1));
    }

    private void send(double x, double y, double z) {
        mc.getConnection().send(new ServerboundMovePlayerPacket.Pos(x, y, z, false));
    }
}
