package com.neoassist.module.impl.render;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.BlockListSetting;
import com.neoassist.module.setting.ColorSetting;
import com.neoassist.module.setting.NumberSetting;
import com.neoassist.util.RenderUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BlockESP extends Module {
    private final NumberSetting radius = new NumberSetting("Radius", "Scan radius in blocks", 12, 4, 32, 1);
    private final ColorSetting color = new ColorSetting("Color", "Outline color", 0x9000FF7F);
    private final BlockListSetting blocks = new BlockListSetting("Blocks", "Blocks to highlight (edit in GUI)",
            "minecraft:diamond_ore", "minecraft:deepslate_diamond_ore", "minecraft:ancient_debris");

    private final List<BlockPos> found = new ArrayList<>();
    private int rescan;

    public BlockESP() {
        super("BlockESP", "Outlines selected blocks nearby (pick blocks in GUI)", Category.WORLD);
        addSettings(radius, color, blocks);
    }

    @Override
    public String getInfo() {
        return String.valueOf(blocks.size());
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
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    pos.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                    if (blocks.contains(level().getBlockState(pos))) {
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
