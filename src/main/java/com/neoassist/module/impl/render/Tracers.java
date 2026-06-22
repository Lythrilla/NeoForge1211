package com.neoassist.module.impl.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.BooleanSetting;
import com.neoassist.module.setting.ColorSetting;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import com.neoassist.util.RenderUtil;

public class Tracers extends Module {
    private final BooleanSetting players = new BooleanSetting("Players", "Trace other players", true);
    private final BooleanSetting hostiles = new BooleanSetting("Hostiles", "Trace hostile mobs", true);
    private final BooleanSetting animals = new BooleanSetting("Animals", "Trace passive animals", false);
    private final ColorSetting color = new ColorSetting("Color", "Line color", 0xC0FF3366);

    public Tracers() {
        super("Tracers", "Draws lines toward nearby entities", Category.RENDER);
        addSettings(players, hostiles, animals, color);
    }

    private boolean isValid(Entity entity) {
        if (!(entity instanceof LivingEntity living) || living == player() || !living.isAlive()) {
            return false;
        }
        if (living instanceof Player) {
            return players.get();
        }
        if (living instanceof Enemy) {
            return hostiles.get();
        }
        if (living instanceof Animal) {
            return animals.get();
        }
        return hostiles.get();
    }

    @Override
    public void onWorldRender(PoseStack poseStack, MultiBufferSource.BufferSource buffer, Vec3 cameraPos, float partial) {
        if (!inGame()) {
            return;
        }
        Vec3 start = player().getEyePosition(partial).subtract(cameraPos);
        int argb = color.get();
        for (Entity entity : level().entitiesForRendering()) {
            if (!isValid(entity)) {
                continue;
            }
            Vec3 target = entity.position().add(0, entity.getBbHeight() * 0.5, 0).subtract(cameraPos);
            RenderUtil.drawLine(poseStack, buffer, start.x, start.y, start.z, target.x, target.y, target.z, argb);
        }
    }
}
