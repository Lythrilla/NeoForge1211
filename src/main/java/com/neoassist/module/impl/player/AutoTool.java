package com.neoassist.module.impl.player;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.BooleanSetting;
import com.neoassist.util.InventoryUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class AutoTool extends Module {
    private final BooleanSetting switchBack = new BooleanSetting("SwitchBack", "Switch back to the previous slot when done", true);

    private int previousSlot = -1;

    public AutoTool() {
        super("AutoTool", "Selects the best tool for the block you mine", Category.PLAYER);
        addSettings(switchBack);
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.screen != null) {
            restorePreviousSlot();
            return;
        }

        boolean mining = mc.options.keyAttack.isDown()
                && mc.hitResult != null
                && mc.hitResult.getType() == HitResult.Type.BLOCK;

        if (mining) {
            BlockPos pos = ((BlockHitResult) mc.hitResult).getBlockPos();
            BlockState state = level().getBlockState(pos);
            if (!state.isAir()) {
                int best = InventoryUtil.findBestToolHotbarSlot(player(), state);
                if (best != -1 && best != player().getInventory().selected) {
                    if (previousSlot == -1) {
                        previousSlot = player().getInventory().selected;
                    }
                    player().getInventory().selected = best;
                }
            }
        } else {
            restorePreviousSlot();
        }
    }

    @Override
    public void onDisable() {
        restorePreviousSlot();
    }

    private void restorePreviousSlot() {
        if (previousSlot == -1) {
            return;
        }
        if (switchBack.get() && mc.player != null) {
            player().getInventory().selected = previousSlot;
        }
        previousSlot = -1;
    }
}
