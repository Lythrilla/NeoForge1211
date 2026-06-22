package com.neoassist.module.impl.world;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.BooleanSetting;
import com.neoassist.module.setting.ModeSetting;
import com.neoassist.module.setting.NumberSetting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class Scaffold extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Scaffold behavior",
            "Normal", "Normal", "Tower");
    private final BooleanSetting autoSwitch = new BooleanSetting("AutoSwitch", "Auto-select blocks from hotbar", true);
    private final BooleanSetting keepY = new BooleanSetting("KeepY", "Only bridge at the Y level when enabled", false);
    private final NumberSetting towerSpeed = new NumberSetting("TowerSpeed", "Upward velocity for tower", 0.42, 0.3, 0.5, 0.01);

    private int startY = -1;
    private int previousSlot = -1;

    public Scaffold() {
        super("Scaffold", "Automatically places blocks beneath you", Category.WORLD);
        towerSpeed.visibleWhen(() -> mode.is("Tower"));
        addSettings(mode, autoSwitch, keepY, towerSpeed);
    }

    @Override
    public String getInfo() {
        return mode.get();
    }

    @Override
    public void onEnable() {
        if (mc.player != null) {
            startY = mc.player.blockPosition().getY();
        }
    }

    @Override
    public void onDisable() {
        restoreSlot();
        startY = -1;
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.screen != null || mc.gameMode == null) {
            return;
        }
        if (autoSwitch.get()) {
            selectBlock();
        }
        if (!(player().getMainHandItem().getItem() instanceof BlockItem)) {
            return;
        }

        BlockPos target = player().blockPosition().below();
        if (keepY.get() && startY != -1) {
            target = new BlockPos(target.getX(), startY - 1, target.getZ());
        }
        if (!level().getBlockState(target).canBeReplaced()) {
            return;
        }

        boolean placed = placeBlock(target);
        if (placed && mode.is("Tower") && mc.options.keyJump.isDown()) {
            Vec3 motion = player().getDeltaMovement();
            player().setDeltaMovement(motion.x, towerSpeed.get(), motion.z);
        }
    }

    private boolean placeBlock(BlockPos target) {
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = target.relative(dir);
            if (level().getBlockState(neighbor).canBeReplaced()) {
                continue;
            }
            Direction face = dir.getOpposite();
            Vec3 hit = Vec3.atCenterOf(neighbor).add(
                    face.getStepX() * 0.5, face.getStepY() * 0.5, face.getStepZ() * 0.5);
            BlockHitResult result = new BlockHitResult(hit, face, neighbor, false);
            mc.gameMode.useItemOn(player(), InteractionHand.MAIN_HAND, result);
            player().swing(InteractionHand.MAIN_HAND);
            return true;
        }
        return false;
    }

    private void selectBlock() {
        if (player().getMainHandItem().getItem() instanceof BlockItem) {
            return;
        }
        for (int i = 0; i < 9; i++) {
            if (player().getInventory().getItem(i).getItem() instanceof BlockItem) {
                if (previousSlot == -1) {
                    previousSlot = player().getInventory().selected;
                }
                player().getInventory().selected = i;
                return;
            }
        }
    }

    private void restoreSlot() {
        if (previousSlot != -1 && mc.player != null) {
            player().getInventory().selected = previousSlot;
            previousSlot = -1;
        }
    }
}
