package com.neoassist.module.impl.render;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.NumberSetting;

import net.minecraft.world.phys.Vec3;

public class FreeCam extends Module {
    private final NumberSetting speed = new NumberSetting("Speed", "Camera fly speed", 1.0, 0.1, 5.0, 0.1);

    private double camX, camY, camZ;
    private float camYaw, camPitch;
    private boolean active;

    public FreeCam() {
        super("FreeCam", "Detach the camera and fly around freely", Category.RENDER);
        addSettings(speed);
    }

    @Override
    public void onEnable() {
        if (mc.player == null) {
            return;
        }
        camX = mc.player.getX();
        camY = mc.player.getY();
        camZ = mc.player.getZ();
        camYaw = mc.player.getYRot();
        camPitch = mc.player.getXRot();
        active = true;
    }

    @Override
    public void onDisable() {
        active = false;
    }

    @Override
    public void onTick() {
        if (!inGame() || !active) {
            return;
        }
        double spd = speed.get() * 0.5;
        double forward = 0;
        double strafe = 0;
        double vertical = 0;

        if (mc.options.keyUp.isDown()) forward += 1;
        if (mc.options.keyDown.isDown()) forward -= 1;
        if (mc.options.keyLeft.isDown()) strafe += 1;
        if (mc.options.keyRight.isDown()) strafe -= 1;
        if (mc.options.keyJump.isDown()) vertical += 1;
        if (mc.options.keyShift.isDown()) vertical -= 1;

        double yaw = Math.toRadians(player().getYRot());
        double dx = (-Math.sin(yaw) * forward + Math.cos(yaw) * strafe) * spd;
        double dz = (Math.cos(yaw) * forward + Math.sin(yaw) * strafe) * spd;
        double dy = vertical * spd;

        camX += dx;
        camY += dy;
        camZ += dz;

        player().setDeltaMovement(Vec3.ZERO);
        player().setPos(camX, camY, camZ);
        player().noPhysics = true;
    }

    public boolean isFreeCamActive() {
        return active && isEnabled();
    }
}
