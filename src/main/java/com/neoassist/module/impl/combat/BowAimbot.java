package com.neoassist.module.impl.combat;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.NumberSetting;
import com.neoassist.util.RotationUtil;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.phys.Vec3;

public class BowAimbot extends Module {
    private final NumberSetting range = new NumberSetting("Range", "Target search range", 48, 8, 128, 1);

    public BowAimbot() {
        super("BowAimbot", "Auto-aims while drawing a bow or crossbow", Category.COMBAT);
        addSettings(range);
    }

    private boolean usingBow() {
        if (!player().isUsingItem()) {
            return false;
        }
        var item = player().getUseItem().getItem();
        return item instanceof BowItem || item instanceof CrossbowItem;
    }

    @Override
    public void onTick() {
        if (!inGame() || !usingBow()) {
            return;
        }
        Entity target = findTarget();
        if (target == null) {
            return;
        }
        Vec3 eyes = player().getEyePosition();
        Vec3 center = target.position().add(0, target.getBbHeight() * 0.5, 0);
        float[] rot = RotationUtil.getRotations(eyes, center);

        // simple ballistic pitch compensation for arrow drop
        double dx = center.x - eyes.x;
        double dz = center.z - eyes.z;
        double dist = Math.sqrt(dx * dx + dz * dz);
        double dy = center.y - eyes.y;
        double v = 3.0;
        double g = 0.05;
        double v2 = v * v;
        double root = v2 * v2 - g * (g * dist * dist + 2 * dy * v2);
        float pitch = rot[1];
        if (root >= 0) {
            double angle = Math.atan((v2 - Math.sqrt(root)) / (g * dist));
            pitch = (float) -Math.toDegrees(angle);
        }

        player().setYRot(rot[0]);
        player().setXRot(pitch);
    }

    private Entity findTarget() {
        double max = range.get();
        Entity best = null;
        double bestDist = max;
        for (Entity entity : level().entitiesForRendering()) {
            if (!(entity instanceof LivingEntity living) || living == player() || !living.isAlive()) {
                continue;
            }
            if (!(living instanceof Player) && !(living instanceof Enemy)) {
                continue;
            }
            double dist = player().distanceTo(entity);
            if (dist < bestDist && player().hasLineOfSight(entity)) {
                bestDist = dist;
                best = entity;
            }
        }
        return best;
    }
}
