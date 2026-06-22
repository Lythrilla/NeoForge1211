package com.neoassist.module.impl.combat;

import com.neoassist.NeoAssist;
import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.impl.render.NoHurtCam;
import com.neoassist.module.setting.ModeSetting;
import com.neoassist.module.setting.NumberSetting;

import net.minecraft.world.phys.Vec3;

public class AntiKnockback extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Knockback cancellation method",
            "Cancel", "Cancel", "Reduce");
    private final NumberSetting strength = new NumberSetting("Strength", "Knockback reduction (%)", 100, 0, 100, 5);

    private int lastHurtTime;
    private int reduceTicks;

    public AntiKnockback() {
        super("AntiKnockback", "Reduces or cancels knockback from hits", Category.COMBAT);
        strength.visibleWhen(() -> mode.is("Reduce"));
        addSettings(mode, strength);
    }

    @Override
    public String getInfo() {
        return mode.get();
    }

    @Override
    public void onEnable() {
        lastHurtTime = 0;
        reduceTicks = 0;
    }

    @Override
    public void onTick() {
        if (!inGame()) {
            return;
        }
        Module noHurtCam = NeoAssist.MODULES.getByName("NoHurtCam");
        int hurtTime = (noHurtCam != null && noHurtCam.isEnabled())
                ? NoHurtCam.realHurtTime
                : player().hurtTime;
        if (hurtTime > lastHurtTime) {
            reduceTicks = 2;
        }
        lastHurtTime = hurtTime;

        if (reduceTicks > 0) {
            reduceTicks--;
            if (mode.is("Cancel")) {
                Vec3 motion = player().getDeltaMovement();
                player().setDeltaMovement(0, motion.y, 0);
            } else {
                double factor = 1.0 - strength.get() / 100.0;
                Vec3 motion = player().getDeltaMovement();
                player().setDeltaMovement(motion.x * factor, motion.y, motion.z * factor);
            }
        }
    }
}
