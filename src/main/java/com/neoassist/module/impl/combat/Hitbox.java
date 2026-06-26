package com.neoassist.module.impl.combat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.BooleanSetting;
import com.neoassist.module.setting.ColorSetting;
import com.neoassist.module.setting.NumberSetting;
import com.neoassist.util.RenderUtil;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Hitbox extends Module {
    /** Read by EntityMixin to enlarge the crosshair pick box. 0 means vanilla. */
    public static volatile float expand = 0.0F;

    private final NumberSetting size = new NumberSetting("Size", "Extra hitbox radius added to entities", 0.5, 0.1, 1.0, 0.05);
    private final BooleanSetting render = new BooleanSetting("Render", "Outline the enlarged hitbox", true);
    private final ColorSetting color = new ColorSetting("Color", "Hitbox outline color", 0xC0FF3030);

    public Hitbox() {
        super("Hitbox", "Enlarges entity hitboxes so they are easier to hit", Category.COMBAT);
        addSettings(size, render, color);
    }

    @Override
    public void onTick() {
        expand = size.getFloat();
    }

    @Override
    public void onEnable() {
        expand = size.getFloat();
    }

    @Override
    public void onDisable() {
        expand = 0.0F;
    }

    @Override
    public void onWorldRender(PoseStack poseStack, MultiBufferSource.BufferSource buffer, Vec3 cameraPos, float partial) {
        if (!inGame() || !render.get()) {
            return;
        }
        float r = size.getFloat();
        int argb = color.get();
        for (Entity entity : level().entitiesForRendering()) {
            if (!(entity instanceof LivingEntity) || entity == player() || !entity.isAlive()) {
                continue;
            }
            AABB box = entity.getBoundingBox().inflate(r).move(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            RenderUtil.drawBox(poseStack, buffer, box, argb);
        }
    }
}
