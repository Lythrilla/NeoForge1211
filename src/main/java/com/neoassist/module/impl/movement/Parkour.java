package com.neoassist.module.impl.movement;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.BooleanSetting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class Parkour extends Module {
    private final BooleanSetting forwardOnly = new BooleanSetting("ForwardOnly", "Only jump while holding forward", true);

    public Parkour() {
        super("Parkour", "Automatically jumps at block edges", Category.MOVEMENT);
        addSettings(forwardOnly);
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.screen != null || !player().onGround() || player().isShiftKeyDown()) {
            return;
        }
        if (forwardOnly.get() && !mc.options.keyUp.isDown()) {
            return;
        }
        Direction direction = player().getDirection();
        BlockPos nextBelow = player().blockPosition().relative(direction).below();
        if (level().getBlockState(nextBelow).isAir()) {
            Vec3 motion = player().getDeltaMovement();
            player().setDeltaMovement(motion.x, 0.42D, motion.z);
        }
    }
}
