package com.neoassist.module.impl.combat;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.NumberSetting;

import net.minecraft.world.phys.Vec3;

public class AntiKnockback extends Module {
    private final NumberSetting strength = new NumberSetting("Strength", "Knockback reduction (%)", 100, 0, 100, 5);

    private int lastHurtTime;
    private int reduceTicks;

    public AntiKnockback() {
        super("AntiKnockback", "Reduces knockback taken from hits", Category.COMBAT);
        addSettings(strength);
    }

    @Override
    public void onTick() {
        if (!inGame()) {
            return;
        }
        int hurtTime = player().hurtTime;
        if (hurtTime > lastHurtTime) {
            reduceTicks = 2;
        }
        lastHurtTime = hurtTime;

        if (reduceTicks > 0) {
            reduceTicks--;
            double factor = 1.0 - strength.get() / 100.0;
            Vec3 motion = player().getDeltaMovement();
            player().setDeltaMovement(motion.x * factor, motion.y, motion.z * factor);
        }
    }
}
