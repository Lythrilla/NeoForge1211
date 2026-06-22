package com.neoassist.module.impl.player;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.ModeSetting;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.phys.Vec3;

public class NoFall extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Anti-fall method",
            "Packet", "Packet", "NoVoid");

    public NoFall() {
        super("NoFall", "Prevents fall damage", Category.PLAYER);
        addSettings(mode);
    }

    @Override
    public String getInfo() {
        return mode.get();
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.getConnection() == null) {
            return;
        }
        if (mode.is("Packet")) {
            tickPacket();
        } else if (mode.is("NoVoid")) {
            tickNoVoid();
        }
    }

    private void tickPacket() {
        if (!player().onGround() && player().fallDistance > 2.0F && player().getDeltaMovement().y < 0) {
            mc.getConnection().send(new ServerboundMovePlayerPacket.StatusOnly(true));
        }
    }

    private void tickNoVoid() {
        if (player().getY() < level().getMinBuildHeight() + 2 && player().getDeltaMovement().y < -0.1) {
            Vec3 motion = player().getDeltaMovement();
            player().setDeltaMovement(motion.x, 0.0, motion.z);
            player().fallDistance = 0;
        }
        if (!player().onGround() && player().fallDistance > 2.0F && player().getDeltaMovement().y < 0) {
            mc.getConnection().send(new ServerboundMovePlayerPacket.StatusOnly(true));
        }
    }
}
