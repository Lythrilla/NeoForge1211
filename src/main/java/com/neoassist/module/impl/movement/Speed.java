package com.neoassist.module.impl.movement;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.ModeSetting;
import com.neoassist.module.setting.NumberSetting;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

public class Speed extends Module {
    private static final double DEFAULT_SPEED = 0.1;

    private final ModeSetting mode = new ModeSetting("Mode", "Speed method",
            "Attribute", "Attribute", "BunnyHop", "Strafe");
    private final NumberSetting speed = new NumberSetting("Speed", "Speed multiplier", 1.5, 1.0, 5.0, 0.1);

    private boolean attributeApplied;

    public Speed() {
        super("Speed", "Increases your movement speed", Category.MOVEMENT);
        addSettings(mode, speed);
    }

    @Override
    public String getInfo() {
        return mode.get() + " " + String.format("%.1fx", speed.get());
    }

    @Override
    public void onTick() {
        if (!inGame()) {
            return;
        }
        if (mode.is("Attribute")) {
            tickAttribute();
            return;
        }
        if (attributeApplied) {
            resetAttribute();
        }
        if (mode.is("BunnyHop")) {
            tickBunnyHop();
        } else if (mode.is("Strafe")) {
            tickStrafe();
        }
    }

    private void tickAttribute() {
        AttributeInstance attr = player().getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null) {
            attr.setBaseValue(DEFAULT_SPEED * speed.get());
            attributeApplied = true;
        }
    }

    /** Restores the vanilla base movement speed this module modified. */
    private void resetAttribute() {
        attributeApplied = false;
        if (mc.player == null) {
            return;
        }
        AttributeInstance attr = player().getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null) {
            attr.setBaseValue(DEFAULT_SPEED);
        }
    }

    private void tickBunnyHop() {
        if (!isMoving() || mc.screen != null) {
            return;
        }
        player().setSprinting(true);
        if (player().onGround()) {
            player().jumpFromGround();
            Vec3 motion = player().getDeltaMovement();
            player().setDeltaMovement(motion.x * speed.get(), motion.y, motion.z * speed.get());
        }
    }

    private void tickStrafe() {
        if (!isMoving() || mc.screen != null) {
            return;
        }
        player().setSprinting(true);
        double yaw = Math.toRadians(player().getYRot());
        double forward = player().zza;
        double strafe = player().xxa;

        double spd = 0.2873 * speed.get();
        if (player().onGround()) {
            player().jumpFromGround();
        }
        Vec3 motion = player().getDeltaMovement();
        double dx = -Math.sin(yaw) * forward + Math.cos(yaw) * strafe;
        double dz = Math.cos(yaw) * forward + Math.sin(yaw) * strafe;
        double len = Math.sqrt(dx * dx + dz * dz);
        if (len > 0) {
            dx = dx / len * spd;
            dz = dz / len * spd;
        }
        player().setDeltaMovement(dx, motion.y, dz);
    }

    private boolean isMoving() {
        return player().zza != 0 || player().xxa != 0;
    }

    @Override
    public void onDisable() {
        if (attributeApplied) {
            resetAttribute();
        }
    }
}
