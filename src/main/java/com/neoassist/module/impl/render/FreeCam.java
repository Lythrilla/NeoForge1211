package com.neoassist.module.impl.render;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.NumberSetting;

import net.minecraft.world.phys.Vec3;

public class FreeCam extends Module {
    private final NumberSetting speed = new NumberSetting("Speed", "Camera fly speed", 1.0, 0.1, 5.0, 0.1);

    private double camX, camY, camZ;
    private double savedX, savedY, savedZ;
    private float savedYaw, savedPitch;
    private boolean active;

    public FreeCam() {
        super("FreeCam", "Detach the camera and fly around freely (noclip)", Category.RENDER);
        addSettings(speed);
    }

    @Override
    public void onEnable() {
        if (mc.player == null) {
            return;
        }
        savedX = mc.player.getX();
        savedY = mc.player.getY();
        savedZ = mc.player.getZ();
        savedYaw = mc.player.getYRot();
        savedPitch = mc.player.getXRot();
        camX = savedX;
        camY = savedY;
        camZ = savedZ;
        active = true;
    }

    @Override
    public void onDisable() {
        if (active && mc.player != null) {
            player().noPhysics = false;
            player().setPos(savedX, savedY, savedZ);
            player().setYRot(savedYaw);
            player().setXRot(savedPitch);
        }
        active = false;
    }

    @Override
    public void onTick() {
        if (!inGame() || !active) {
            return;
        }
        player().noPhysics = true;

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
        double pitch = Math.toRadians(player().getXRot());

        double dx = (-Math.sin(yaw) * forward + Math.cos(yaw) * strafe) * spd;
        double dz = (Math.cos(yaw) * forward + Math.sin(yaw) * strafe) * spd;
        double dy = vertical * spd;

        if (forward != 0) {
            dy += -Math.sin(pitch) * forward * spd;
            double horizScale = Math.cos(pitch);
            dx = -Math.sin(yaw) * forward * horizScale * spd + Math.cos(yaw) * strafe * spd;
            dz = Math.cos(yaw) * forward * horizScale * spd + Math.sin(yaw) * strafe * spd;
        }
        if (mc.options.keyJump.isDown()) dy = spd;
        if (mc.options.keyShift.isDown()) dy = -spd;

        camX += dx;
        camY += dy;
        camZ += dz;

        player().setDeltaMovement(Vec3.ZERO);
        player().setPos(camX, camY, camZ);
    }

    public boolean isFreeCamActive() {
        return active && isEnabled();
    }
}
