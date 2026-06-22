package com.neoassist.module.impl.combat;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.BooleanSetting;
import com.neoassist.module.setting.NumberSetting;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

public class AutoShield extends Module {
    private final NumberSetting range = new NumberSetting("Range", "Raise shield when threats are this close", 5.0, 2.0, 8.0, 0.5);
    private final BooleanSetting players = new BooleanSetting("Players", "Block near other players", true);
    private final BooleanSetting hostiles = new BooleanSetting("Hostiles", "Block near hostile mobs", true);
    private final BooleanSetting wallCheck = new BooleanSetting("WallCheck", "Require line of sight to the threat", true);

    private boolean blocking;

    public AutoShield() {
        super("AutoShield", "Automatically raises your shield near threats", Category.COMBAT);
        addSettings(range, players, hostiles, wallCheck);
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.screen != null || !holdingShield()) {
            release();
            return;
        }
        if (hasThreat()) {
            mc.options.keyUse.setDown(true);
            blocking = true;
        } else {
            release();
        }
    }

    @Override
    public void onDisable() {
        release();
    }

    private boolean holdingShield() {
        return player().getMainHandItem().is(Items.SHIELD) || player().getOffhandItem().is(Items.SHIELD);
    }

    private boolean hasThreat() {
        double maxDistance = range.get();
        for (Entity entity : level().entitiesForRendering()) {
            if (!isThreat(entity)) {
                continue;
            }
            if (player().distanceTo(entity) > maxDistance) {
                continue;
            }
            if (wallCheck.get() && !player().hasLineOfSight(entity)) {
                continue;
            }
            return true;
        }
        return false;
    }

    private boolean isThreat(Entity entity) {
        if (!(entity instanceof LivingEntity living) || living == player() || !living.isAlive()) {
            return false;
        }
        if (living instanceof Player) {
            return players.get();
        }
        return living instanceof Enemy && hostiles.get();
    }

    private void release() {
        if (blocking) {
            mc.options.keyUse.setDown(false);
            blocking = false;
        }
    }
}
