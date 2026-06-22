package com.neoassist.module.impl.movement;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.NumberSetting;

import net.minecraft.world.phys.Vec3;

public class Spider extends Module {
    private final NumberSetting speed = new NumberSetting("Speed", "Climb speed", 0.2, 0.1, 0.5, 0.05);

    public Spider() {
        super("Spider", "Climb up walls like a spider", Category.MOVEMENT);
        addSettings(speed);
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.screen != null) {
            return;
        }
        if (player().horizontalCollision && (player().zza != 0 || player().xxa != 0)) {
            Vec3 motion = player().getDeltaMovement();
            player().setDeltaMovement(motion.x, speed.get(), motion.z);
            player().fallDistance = 0;
        }
    }
}
