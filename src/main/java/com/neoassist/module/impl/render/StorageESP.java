package com.neoassist.module.impl.render;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.ColorSetting;
import com.neoassist.module.setting.NumberSetting;
import com.neoassist.util.RenderUtil;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class StorageESP extends Module {
    private final NumberSetting radius = new NumberSetting("Radius", "Scan radius in blocks", 16, 4, 48, 1);
    private final ColorSetting color = new ColorSetting("Color", "Outline color", 0xA0FFC83C);

    private final List<BlockPos> found = new ArrayList<>();
    private int rescan;

    public StorageESP() {
        super("StorageESP", "Outlines nearby containers (chests, barrels, ...)", Category.RENDER);
        addSettings(radius, color);
    }

    private static boolean isContainer(Block block) {
        return block instanceof ChestBlock
                || block instanceof EnderChestBlock
                || block instanceof BarrelBlock
                || block instanceof ShulkerBoxBlock
                || block instanceof HopperBlock
                || block instanceof DispenserBlock
                || block instanceof BrewingStandBlock
                || block instanceof AbstractFurnaceBlock;
    }

    @Override
    public void onTick() {
        if (!inGame()) {
            return;
        }
        if (rescan-- > 0) {
            return;
        }
        rescan = 10;
        found.clear();
        int r = radius.getInt();
        BlockPos origin = player().blockPosition();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    pos.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                    if (isContainer(level().getBlockState(pos).getBlock())) {
                        found.add(pos.immutable());
                        if (found.size() >= 2000) {
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onWorldRender(PoseStack poseStack, MultiBufferSource.BufferSource buffer, Vec3 cameraPos, float partial) {
        if (found.isEmpty()) {
            return;
        }
        int argb = color.get();
        for (BlockPos pos : found) {
            AABB box = new AABB(pos).move(-cameraPos.x, -cameraPos.y, -cameraPos.z).deflate(0.002);
            RenderUtil.drawBox(poseStack, buffer, box, argb);
        }
    }
}
