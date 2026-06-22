package com.neoassist.module.impl.world;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.NumberSetting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class Nuker extends Module {
    private final NumberSetting radius = new NumberSetting("Radius", "Break radius in blocks", 4, 1, 6, 1);

    private BlockPos current;

    public Nuker() {
        super("Nuker", "Automatically breaks blocks around you", Category.WORLD);
        addSettings(radius);
    }

    private boolean breakable(BlockPos pos) {
        BlockState state = level().getBlockState(pos);
        if (state.isAir() || !state.getFluidState().isEmpty()) {
            return false;
        }
        return state.getDestroySpeed(level(), pos) >= 0;
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.gameMode == null) {
            return;
        }
        if (current != null && breakable(current)) {
            mc.gameMode.continueDestroyBlock(current, Direction.UP);
            return;
        }
        current = findNearest();
        if (current != null) {
            mc.gameMode.startDestroyBlock(current, Direction.UP);
        }
    }

    private BlockPos findNearest() {
        int r = radius.getInt();
        BlockPos origin = player().blockPosition();
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    pos.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                    if (!breakable(pos)) {
                        continue;
                    }
                    double dist = player().distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    if (dist < bestDist) {
                        bestDist = dist;
                        best = pos.immutable();
                    }
                }
            }
        }
        return best;
    }
}
