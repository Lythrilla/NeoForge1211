package com.neoassist.module.impl.render;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.ColorSetting;
import com.neoassist.module.setting.ModeSetting;
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
    private final ModeSetting mode = new ModeSetting("Mode", "Rendering style",
            "Box", "Box", "Filled");
    private final NumberSetting radius = new NumberSetting("Radius", "Scan radius in blocks", 16, 4, 48, 1);
    private final ColorSetting chestColor = new ColorSetting("ChestColor", "Chest/barrel color", 0xA0FFC83C);
    private final ColorSetting enderColor = new ColorSetting("EnderColor", "Ender chest color", 0xA0CC33FF);
    private final ColorSetting shulkerColor = new ColorSetting("ShulkerColor", "Shulker box color", 0xA0FF66AA);
    private final ColorSetting otherColor = new ColorSetting("OtherColor", "Other container color", 0xA0AAAAAA);

    private static final int TYPE_CHEST = 0;
    private static final int TYPE_ENDER = 1;
    private static final int TYPE_SHULKER = 2;
    private static final int TYPE_OTHER = 3;

    private final List<BlockPos> found = new ArrayList<>();
    private final List<Integer> types = new ArrayList<>();
    private int rescan;

    public StorageESP() {
        super("StorageESP", "Outlines nearby containers (chests, barrels, ...)", Category.RENDER);
        addSettings(mode, radius, chestColor, enderColor, shulkerColor, otherColor);
    }

    @Override
    public String getInfo() {
        return mode.get();
    }

    private static int containerType(Block block) {
        if (block instanceof ChestBlock || block instanceof BarrelBlock) return TYPE_CHEST;
        if (block instanceof EnderChestBlock) return TYPE_ENDER;
        if (block instanceof ShulkerBoxBlock) return TYPE_SHULKER;
        if (block instanceof HopperBlock || block instanceof DispenserBlock
                || block instanceof BrewingStandBlock || block instanceof AbstractFurnaceBlock) {
            return TYPE_OTHER;
        }
        return -1;
    }

    private int colorForType(int type) {
        return switch (type) {
            case TYPE_CHEST -> chestColor.get();
            case TYPE_ENDER -> enderColor.get();
            case TYPE_SHULKER -> shulkerColor.get();
            default -> otherColor.get();
        };
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
        types.clear();
        int r = radius.getInt();
        BlockPos origin = player().blockPosition();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    pos.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                    int type = containerType(level().getBlockState(pos).getBlock());
                    if (type >= 0) {
                        found.add(pos.immutable());
                        types.add(type);
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
        boolean filled = mode.is("Filled");
        for (int i = 0; i < found.size(); i++) {
            BlockPos pos = found.get(i);
            int argb = colorForType(types.get(i));
            AABB box = new AABB(pos).move(-cameraPos.x, -cameraPos.y, -cameraPos.z).deflate(0.002);
            if (filled) {
                RenderUtil.drawFilledBox(poseStack, buffer, box, argb);
            }
            RenderUtil.drawBox(poseStack, buffer, box, argb);
        }
    }
}
