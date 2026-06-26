package com.neoassist.module.impl.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.BooleanSetting;
import com.neoassist.module.setting.ColorSetting;
import com.neoassist.module.setting.NumberSetting;
import com.neoassist.util.RenderUtil;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Highlights loose items so they are easy to spot from afar: dropped item entities and the items
 * shown inside item frames. Container <em>blocks</em> are handled by StorageESP; a container's
 * contents are not known client-side until it is opened, so they cannot be highlighted here.
 */
public class ItemESP extends Module {
    private final BooleanSetting drops = new BooleanSetting("Drops", "Highlight dropped items on the ground", true);
    private final BooleanSetting frames = new BooleanSetting("Frames", "Highlight items inside item frames", true);
    private final BooleanSetting tracer = new BooleanSetting("Tracer", "Draw a line toward each item", false);
    private final ColorSetting color = new ColorSetting("Color", "Highlight color", 0xC033CCFF);
    private final NumberSetting range = new NumberSetting("Range", "Maximum render distance", 64, 16, 128, 8);

    public ItemESP() {
        super("ItemESP", "Highlights dropped items and item frames so loot is easy to find", Category.RENDER);
        addSettings(drops, frames, tracer, color, range);
    }

    private boolean isValid(Entity entity) {
        if (entity instanceof ItemEntity) {
            return drops.get();
        }
        if (entity instanceof ItemFrame frame) {
            return frames.get() && !frame.getItem().isEmpty();
        }
        return false;
    }

    @Override
    public void onWorldRender(PoseStack poseStack, MultiBufferSource.BufferSource buffer, Vec3 cameraPos, float partial) {
        if (!inGame()) {
            return;
        }
        int argb = color.get();
        double maxRange = range.get();
        Vec3 start = tracer.get() ? player().getEyePosition(partial).subtract(cameraPos) : null;
        for (Entity entity : level().entitiesForRendering()) {
            if (!isValid(entity) || player().distanceTo(entity) > maxRange) {
                continue;
            }
            AABB box = entity.getBoundingBox().move(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            RenderUtil.drawBox(poseStack, buffer, box, argb);
            if (start != null) {
                Vec3 target = entity.position().add(0, entity.getBbHeight() * 0.5, 0).subtract(cameraPos);
                RenderUtil.drawLine(poseStack, buffer, start.x, start.y, start.z, target.x, target.y, target.z, argb);
            }
        }
    }
}
