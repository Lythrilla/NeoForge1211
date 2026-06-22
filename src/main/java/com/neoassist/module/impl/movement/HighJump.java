package com.neoassist.module.impl.movement;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.NumberSetting;

import net.minecraft.world.phys.Vec3;

public class HighJump extends Module {
    private final NumberSetting height = new NumberSetting("Height", "Jump velocity", 0.7, 0.42, 1.5, 0.01);

    public HighJump() {
        super("HighJump", "Jump much higher than normal", Category.MOVEMENT);
        addSettings(height);
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.screen != null) {
            return;
        }
        if (player().onGround() && mc.options.keyJump.isDown()) {
            Vec3 motion = player().getDeltaMovement();
            player().setDeltaMovement(motion.x, height.get(), motion.z);
        }
    }
}
