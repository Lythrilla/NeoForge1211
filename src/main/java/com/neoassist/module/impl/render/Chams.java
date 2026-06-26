package com.neoassist.module.impl.render;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.impl.combat.KillAura;
import com.neoassist.module.setting.BooleanSetting;
import com.neoassist.module.setting.NumberSetting;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

public class Chams extends Module {
    private final BooleanSetting players = new BooleanSetting("Players", "Glow other players", true);
    private final BooleanSetting hostiles = new BooleanSetting("Hostiles", "Glow hostile mobs", true);
    private final BooleanSetting animals = new BooleanSetting("Animals", "Glow passive animals", false);
    private final BooleanSetting invisibles = new BooleanSetting("Invisibles", "Glow invisible entities", false);
    private final NumberSetting range = new NumberSetting("Range", "Maximum glow distance", 64, 16, 128, 8);

    public Chams() {
        super("Chams", "Forces a glowing outline on entities (visible through walls)", Category.RENDER);
        addSettings(players, hostiles, animals, invisibles, range);
    }

    private boolean shouldGlow(Entity entity) {
        if (!(entity instanceof LivingEntity living) || living == player() || !living.isAlive()) {
            return false;
        }
        if (KillAura.isWhitelistedType(entity)) {
            return false;
        }
        if (player().distanceTo(entity) > range.get()) {
            return false;
        }
        if (living.isInvisible() && !invisibles.get()) {
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
    public void onTick() {
        if (!inGame()) {
            return;
        }
        for (Entity entity : level().entitiesForRendering()) {
            if (entity instanceof LivingEntity && entity != player()) {
                entity.setGlowingTag(shouldGlow(entity));
            }
        }
    }

    @Override
    public void onDisable() {
        if (mc.level == null) {
            return;
        }
        for (Entity entity : level().entitiesForRendering()) {
            entity.setGlowingTag(false);
        }
    }
}
