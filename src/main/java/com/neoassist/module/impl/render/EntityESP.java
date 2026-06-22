package com.neoassist.module.impl.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.BooleanSetting;
import com.neoassist.module.setting.ColorSetting;
import com.neoassist.util.RenderUtil;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntityESP extends Module {
    private final BooleanSetting players = new BooleanSetting("Players", "Box other players", true);
    private final BooleanSetting hostiles = new BooleanSetting("Hostiles", "Box hostile mobs", true);
    private final BooleanSetting animals = new BooleanSetting("Animals", "Box passive animals", false);
    private final ColorSetting color = new ColorSetting("Color", "Box color", 0xC0FF5555);

    public EntityESP() {
        super("EntityESP", "Draws boxes around living entities", Category.RENDER);
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
        int argb = color.get();
        for (Entity entity : level().entitiesForRendering()) {
            if (!isValid(entity)) {
                continue;
            }
            AABB box = entity.getBoundingBox().move(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            RenderUtil.drawBox(poseStack, buffer, box, argb);
        }
    }
}
