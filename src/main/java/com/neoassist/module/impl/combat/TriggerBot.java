package com.neoassist.module.impl.combat;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.BooleanSetting;
import com.neoassist.module.setting.NumberSetting;

import com.neoassist.module.impl.misc.MiddleClickFriend;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class TriggerBot extends Module {
    private final NumberSetting range = new NumberSetting("Range", "Maximum crosshair attack range", 4.0, 2.0, 6.0, 0.1);
    private final NumberSetting delay = new NumberSetting("Delay", "Ticks between attacks", 2, 0, 20, 1);
    private final BooleanSetting targetPlayers = new BooleanSetting("Players", "Target other players", true);
    private final BooleanSetting targetMobs = new BooleanSetting("Hostiles", "Target hostile mobs", true);
    private final BooleanSetting targetAnimals = new BooleanSetting("Animals", "Target passive animals", false);
    private final BooleanSetting onlyCharged = new BooleanSetting("OnlyCharged", "Wait for full attack charge", true);

    private int ticks;

    public TriggerBot() {
        super("TriggerBot", "Attacks valid entities under your crosshair", Category.COMBAT);
        addSettings(range, delay, targetPlayers, targetMobs, targetAnimals, onlyCharged);
    }

    @Override
    public void onEnable() {
        ticks = 0;
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.screen != null || mc.gameMode == null) {
            return;
        }
        if (ticks > 0) {
            ticks--;
            return;
        }
        if (onlyCharged.get() && player().getAttackStrengthScale(0.0F) < 1.0F) {
            return;
        }
        if (mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.ENTITY) {
            return;
        }
        Entity target = ((EntityHitResult) mc.hitResult).getEntity();
        if (!isValid(target) || player().distanceTo(target) > range.get()) {
            return;
        }
        gameMode().attack(player(), target);
        player().swing(InteractionHand.MAIN_HAND);
        ticks = delay.getInt();
    }

    private boolean isValid(Entity entity) {
        if (!(entity instanceof LivingEntity living) || living == player() || !living.isAlive()) {
            return false;
        }
        if (MiddleClickFriend.isWhitelisted(entity)) {
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
}
