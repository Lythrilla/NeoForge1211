package com.neoassist.module.impl.world;

import com.neoassist.module.Category;
import com.neoassist.module.Module;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class Scaffold extends Module {
    public Scaffold() {
        super("Scaffold", "Automatically places blocks beneath you", Category.WORLD);
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.screen != null || mc.gameMode == null) {
            return;
        }
        if (!(player().getMainHandItem().getItem() instanceof BlockItem)) {
            return;
        }
        BlockPos target = player().blockPosition().below();
        if (!level().getBlockState(target).canBeReplaced()) {
            return;
        }
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = target.relative(dir);
            if (level().getBlockState(neighbor).isAir() || !level().getBlockState(neighbor).getFluidState().isEmpty()) {
                continue;
            }
            Direction face = dir.getOpposite();
            Vec3 hit = Vec3.atCenterOf(neighbor).add(face.getStepX() * 0.5, face.getStepY() * 0.5, face.getStepZ() * 0.5);
            BlockHitResult result = new BlockHitResult(hit, face, neighbor, false);
            mc.gameMode.useItemOn(player(), InteractionHand.MAIN_HAND, result);
            player().swing(InteractionHand.MAIN_HAND);
            return;
        }
    }
}
