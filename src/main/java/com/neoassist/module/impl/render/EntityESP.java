package com.neoassist.module.impl.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.BooleanSetting;
import com.neoassist.module.setting.ColorSetting;
import com.neoassist.module.setting.ModeSetting;
import com.neoassist.module.setting.NumberSetting;
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
    private final ModeSetting mode = new ModeSetting("Mode", "Rendering style",
            "Box", "Box", "Glow");
    private final BooleanSetting players = new BooleanSetting("Players", "Highlight other players", true);
    private final BooleanSetting hostiles = new BooleanSetting("Hostiles", "Highlight hostile mobs", true);
    private final BooleanSetting animals = new BooleanSetting("Animals", "Highlight passive animals", false);
    private final ColorSetting playerColor = new ColorSetting("PlayerColor", "Color for players", 0xC0FF5555);
    private final ColorSetting hostileColor = new ColorSetting("HostileColor", "Color for hostiles", 0xC0FF9900);
    private final ColorSetting animalColor = new ColorSetting("AnimalColor", "Color for animals", 0xC055FF55);
    private final NumberSetting range = new NumberSetting("Range", "Maximum render distance", 64, 16, 128, 8);

    private boolean glowApplied;

    public EntityESP() {
        super("EntityESP", "Highlights living entities (Box or Glow modes)", Category.RENDER);
        addSettings(mode, players, hostiles, animals, playerColor, hostileColor, animalColor, range);
    }

    @Override
    public String getInfo() {
        return mode.get();
    }

    private boolean isValid(Entity entity) {
        if (!(entity instanceof LivingEntity living) || living == player() || !living.isAlive()) {
            return false;
        }
        if (player().distanceTo(entity) > range.get()) {
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

    private int colorFor(Entity entity) {
        if (entity instanceof Player) return playerColor.get();
        if (entity instanceof Enemy) return hostileColor.get();
        if (entity instanceof Animal) return animalColor.get();
        return hostileColor.get();
    }

    @Override
    public void onTick() {
        if (!inGame()) {
            return;
        }
        if (mode.is("Glow")) {
            for (Entity entity : level().entitiesForRendering()) {
                if (entity instanceof LivingEntity && entity != player()) {
                    entity.setGlowingTag(isValid(entity));
                }
            }
            glowApplied = true;
        } else if (glowApplied) {
            clearGlow();
        }
    }

    @Override
    public void onWorldRender(PoseStack poseStack, MultiBufferSource.BufferSource buffer, Vec3 cameraPos, float partial) {
        if (!inGame() || !mode.is("Box")) {
            return;
        }
        for (Entity entity : level().entitiesForRendering()) {
            if (!isValid(entity)) {
                continue;
            }
            AABB box = entity.getBoundingBox().move(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            RenderUtil.drawBox(poseStack, buffer, box, colorFor(entity));
        }
    }

    @Override
    public void onDisable() {
        clearGlow();
    }

    /** Removes glow tags this module applied; safe to call when not in Glow mode. */
    private void clearGlow() {
        glowApplied = false;
        if (mc.level == null) {
            return;
        }
        for (Entity entity : level().entitiesForRendering()) {
            if (entity instanceof LivingEntity && entity != player()) {
                entity.setGlowingTag(false);
            }
        }
    }
}
