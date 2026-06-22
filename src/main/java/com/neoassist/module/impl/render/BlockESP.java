package com.neoassist.module.impl.render;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.BlockListSetting;
import com.neoassist.module.setting.BooleanSetting;
import com.neoassist.module.setting.ColorSetting;
import com.neoassist.module.setting.ModeSetting;
import com.neoassist.module.setting.NumberSetting;
import com.neoassist.util.RenderUtil;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BlockESP extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Rendering style",
            "Box", "Box", "Filled", "Both");
    private final NumberSetting radius = new NumberSetting("Radius", "Scan radius in blocks", 48, 8, 128, 4);
    private final NumberSetting lineWidth = new NumberSetting("LineWidth", "Outline thickness visual emphasis", 1.0, 0.5, 3.0, 0.5);
    private final BooleanSetting tracerLine = new BooleanSetting("Tracer", "Draw a line from crosshair to each block", false);
    private final ColorSetting color = new ColorSetting("Color", "Outline color", 0xC000FF7F);
    private final BlockListSetting blocks = new BlockListSetting("Blocks", "Blocks to highlight (edit in GUI)",
            "minecraft:diamond_ore", "minecraft:deepslate_diamond_ore",
            "minecraft:ancient_debris", "minecraft:emerald_ore", "minecraft:deepslate_emerald_ore");

    private final List<BlockPos> found = new ArrayList<>();
    private int rescan;

    public BlockESP() {
        super("BlockESP", "Outlines selected blocks nearby (pick blocks in GUI)", Category.WORLD);
        addSettings(mode, radius, lineWidth, tracerLine, color, blocks);
    }

    @Override
    public String getInfo() {
        return found.size() > 0 ? mode.get() + " " + found.size() : mode.get();
    }

    @Override
    public void onTick() {
        if (!inGame()) {
            return;
        }
        if (rescan-- > 0) {
            return;
        }
        int r = radius.getInt();
        rescan = r > 64 ? 40 : (r > 32 ? 20 : 10);
        scan();
    }

    private void scan() {
        found.clear();
        if (blocks.size() == 0) {
            return;
        }
        int r = radius.getInt();
        BlockPos origin = player().blockPosition();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int rSq = r * r;
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    if (x * x + y * y + z * z > rSq) {
                        continue;
                    }
                    pos.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                    if (blocks.contains(level().getBlockState(pos))) {
                        found.add(pos.immutable());
                        if (found.size() >= 5000) {
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
        boolean drawFilled = mode.is("Filled") || mode.is("Both");
        boolean drawBox = mode.is("Box") || mode.is("Both");

        for (BlockPos pos : found) {
            AABB box = new AABB(pos).move(-cameraPos.x, -cameraPos.y, -cameraPos.z).deflate(0.002);
            if (drawFilled) {
                RenderUtil.drawFilledBox(poseStack, buffer, box, argb);
            }
            if (drawBox) {
                RenderUtil.drawBox(poseStack, buffer, box, argb);
            }
        }

        if (tracerLine.get()) {
            for (BlockPos pos : found) {
                double bx = pos.getX() + 0.5 - cameraPos.x;
                double by = pos.getY() + 0.5 - cameraPos.y;
                double bz = pos.getZ() + 0.5 - cameraPos.z;
                RenderUtil.drawLine(poseStack, buffer, 0, 0, 0, bx, by, bz, argb);
            }
        }
    }
}
