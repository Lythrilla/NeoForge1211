package com.neoassist.module.impl.movement;

import com.neoassist.module.Category;
import com.neoassist.module.Module;

import net.minecraft.world.phys.Vec3;

public class Jesus extends Module {
    public Jesus() {
        super("Jesus", "Walk on top of water", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.screen != null) {
            return;
        }
        if (player().isInWater() && !player().isShiftKeyDown()) {
            Vec3 m = player().getDeltaMovement();
            if (m.y < 0.11) {
                player().setDeltaMovement(m.x, 0.11, m.z);
            }
            player().fallDistance = 0;
        }
    }
}
