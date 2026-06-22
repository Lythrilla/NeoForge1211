package com.neoassist.module.impl.movement;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.ModeSetting;
import com.neoassist.module.setting.NumberSetting;

import net.minecraft.world.phys.Vec3;

public class Flight extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Flight method",
            "Velocity", "Velocity", "Creative", "Glide");
    private final NumberSetting speed = new NumberSetting("Speed", "Flight speed multiplier", 1.0, 0.5, 5.0, 0.1);
    private final NumberSetting glideSpeed = new NumberSetting("GlideSpeed", "Downward glide rate", 0.04, 0.01, 0.1, 0.01);

    public Flight() {
        super("Flight", "Fly in different modes (Velocity/Creative/Glide)", Category.MOVEMENT);
        glideSpeed.visibleWhen(() -> mode.is("Glide"));
        addSettings(mode, speed, glideSpeed);
    }

    @Override
    public String getInfo() {
        return mode.get();
    }

    @Override
    public void onEnable() {
        if (mc.player == null) {
            return;
        }
        if (mode.is("Creative")) {
            player().getAbilities().mayfly = true;
            player().onUpdateAbilities();
        }
    }

    @Override
    public void onTick() {
        if (!inGame()) {
            return;
        }
        if (mode.is("Creative")) {
            tickCreative();
        } else if (mode.is("Velocity")) {
            tickVelocity();
        } else if (mode.is("Glide")) {
            tickGlide();
        }
    }

    private void tickCreative() {
        player().getAbilities().mayfly = true;
        player().getAbilities().setFlyingSpeed((float) (0.05 * speed.get()));
    }

    private void tickVelocity() {
        double spd = speed.get() * 0.5;
        double forward = 0, strafe = 0, vertical = 0;

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

        if (forward == 0 && strafe == 0 && vertical == 0) {
            player().setDeltaMovement(Vec3.ZERO);
        } else {
            player().setDeltaMovement(dx, dy, dz);
        }
        player().fallDistance = 0;
    }

    private void tickGlide() {
        Vec3 motion = player().getDeltaMovement();
        if (motion.y < -glideSpeed.get()) {
            double spd = speed.get();
            double yaw = Math.toRadians(player().getYRot());
            double forward = 0;
            if (mc.options.keyUp.isDown()) forward += 1;
            if (mc.options.keyDown.isDown()) forward -= 1;

            double dx = -Math.sin(yaw) * forward * spd * 0.3;
            double dz = Math.cos(yaw) * forward * spd * 0.3;
            player().setDeltaMovement(motion.x + dx, -glideSpeed.get(), motion.z + dz);
        }
        player().fallDistance = 0;
    }

    @Override
    public void onDisable() {
        if (mc.player == null) {
            return;
        }
        if (mode.is("Creative")) {
            boolean creative = player().isCreative();
            player().getAbilities().flying = false;
            if (!creative) {
                player().getAbilities().mayfly = false;
            }
            player().getAbilities().setFlyingSpeed(0.05F);
            player().onUpdateAbilities();
        }
    }
}
