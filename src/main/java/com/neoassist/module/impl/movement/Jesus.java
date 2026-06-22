package com.neoassist.module.impl.movement;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.ModeSetting;
import com.neoassist.module.setting.NumberSetting;

import net.minecraft.world.phys.Vec3;

public class Jesus extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Water walk method",
            "Solid", "Solid", "Dolphin");
    private final NumberSetting dolphinSpeed = new NumberSetting("DolphinSpeed", "Swim speed multiplier", 2.0, 1.0, 5.0, 0.1);

    public Jesus() {
        super("Jesus", "Walk on water/lava or swim fast", Category.MOVEMENT);
        dolphinSpeed.visibleWhen(() -> mode.is("Dolphin"));
        addSettings(mode, dolphinSpeed);
    }

    @Override
    public String getInfo() {
        return mode.get();
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.screen != null) {
            return;
        }
        if (mode.is("Solid")) {
            tickSolid();
        } else if (mode.is("Dolphin")) {
            tickDolphin();
        }
    }

    private void tickSolid() {
        if ((player().isInWater() || player().isInLava()) && !player().isShiftKeyDown()) {
            Vec3 m = player().getDeltaMovement();
            if (m.y < 0.11) {
                player().setDeltaMovement(m.x, 0.11, m.z);
            }
            player().fallDistance = 0;
        }
    }

    private void tickDolphin() {
        if (!player().isInWater() && !player().isInLava()) {
            return;
        }
        double spd = dolphinSpeed.get();
        Vec3 motion = player().getDeltaMovement();
        double yaw = Math.toRadians(player().getYRot());
        double pitch = Math.toRadians(player().getXRot());

        double forward = 0;
        if (mc.options.keyUp.isDown()) forward += 1;
        if (mc.options.keyDown.isDown()) forward -= 1;

        double vertical = 0;
        if (mc.options.keyJump.isDown()) vertical += 0.4;
        if (mc.options.keyShift.isDown()) vertical -= 0.4;

        if (forward != 0) {
            double dx = -Math.sin(yaw) * Math.cos(pitch) * forward * 0.3 * spd;
            double dy = -Math.sin(pitch) * forward * 0.3 * spd;
            double dz = Math.cos(yaw) * Math.cos(pitch) * forward * 0.3 * spd;
            player().setDeltaMovement(dx, dy + vertical, dz);
        } else if (vertical != 0) {
            player().setDeltaMovement(motion.x * 0.9, vertical, motion.z * 0.9);
        }
    }
}
