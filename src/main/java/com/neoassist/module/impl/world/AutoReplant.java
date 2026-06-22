package com.neoassist.module.impl.world;

import java.util.Set;

import com.neoassist.module.Category;
import com.neoassist.module.Module;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class AutoReplant extends Module {
    private static final Set<Item> SEEDS = Set.of(
            Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.CARROT, Items.POTATO);

    public AutoReplant() {
        super("AutoReplant", "Plants seeds on empty farmland you look at", Category.WORLD);
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.screen != null || mc.gameMode == null) {
            return;
        }
        if (mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }
        BlockPos pos = ((BlockHitResult) mc.hitResult).getBlockPos();
        if (!(level().getBlockState(pos).getBlock() instanceof FarmBlock)) {
            return;
        }
        if (!level().getBlockState(pos.above()).isAir()) {
            return;
        }
        if (!selectSeed()) {
            return;
        }
        Vec3 hit = new Vec3(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
        BlockHitResult result = new BlockHitResult(hit, net.minecraft.core.Direction.UP, pos, false);
        mc.gameMode.useItemOn(player(), InteractionHand.MAIN_HAND, result);
        player().swing(InteractionHand.MAIN_HAND);
    }

    private boolean selectSeed() {
        if (SEEDS.contains(player().getMainHandItem().getItem())) {
            return true;
        }
        for (int i = 0; i < 9; i++) {
            if (SEEDS.contains(player().getInventory().getItem(i).getItem())) {
                player().getInventory().selected = i;
                return true;
            }
        }
        return false;
    }
}
