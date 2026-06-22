package com.neoassist.module.impl.combat;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.BooleanSetting;
import com.neoassist.module.setting.NumberSetting;
import com.neoassist.util.RotationUtil;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

public class KillAura extends Module {
    private final NumberSetting range = new NumberSetting("Range", "Attack reach in blocks", 4.0, 2.0, 6.0, 0.1);
    private final NumberSetting delay = new NumberSetting("Delay", "Ticks between attacks", 2, 0, 20, 1);
    private final BooleanSetting targetPlayers = new BooleanSetting("Players", "Target other players", true);
    private final BooleanSetting targetMobs = new BooleanSetting("Hostiles", "Target hostile mobs", true);
    private final BooleanSetting targetAnimals = new BooleanSetting("Animals", "Target passive animals", false);
    private final BooleanSetting rotate = new BooleanSetting("Rotate", "Snap rotation to the target", true);
    private final BooleanSetting onlyCharged = new BooleanSetting("OnlyCharged", "Wait for full attack charge", true);
    private final BooleanSetting wallCheck = new BooleanSetting("WallCheck", "Require line of sight", true);

    private int ticks;

    public KillAura() {
        super("KillAura", "Automatically attacks nearby entities", Category.COMBAT);
        addSettings(range, delay, targetPlayers, targetMobs, targetAnimals, rotate, onlyCharged, wallCheck);
    }

    @Override
    public void onTick() {
        if (!inGame()) {
            return;
        }
        if (ticks > 0) {
            ticks--;
            return;
        }
        if (onlyCharged.get() && player().getAttackStrengthScale(0.0F) < 1.0F) {
            return;
        }

        Entity target = findTarget();
        if (target == null) {
            return;
        }

        if (rotate.get()) {
            float[] rot = RotationUtil.getRotationsToEntity(player().getEyePosition(), target);
            player().setYRot(rot[0]);
            player().setXRot(rot[1]);
        }

        gameMode().attack(player(), target);
        player().swing(InteractionHand.MAIN_HAND);
        ticks = delay.getInt();
    }

    private boolean isValid(Entity entity) {
        if (!(entity instanceof LivingEntity living) || living == player() || !living.isAlive()) {
            return false;
        }
        if (living instanceof Player) {
            return targetPlayers.get();
        }
        if (living instanceof Enemy) {
            return targetMobs.get();
        }
        if (living instanceof Animal) {
            return targetAnimals.get();
        }
        return targetMobs.get();
    }

    private Entity findTarget() {
        double maxRange = range.get();
        Entity best = null;
        double bestDist = maxRange;
        for (Entity entity : level().entitiesForRendering()) {
            if (!isValid(entity)) {
                continue;
            }
            double dist = player().distanceTo(entity);
            if (dist > maxRange) {
                continue;
            }
            if (wallCheck.get() && !player().hasLineOfSight(entity)) {
                continue;
            }
            if (dist < bestDist) {
                bestDist = dist;
                best = entity;
            }
        }
        return best;
    }
}
