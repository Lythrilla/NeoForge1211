package com.neoassist.module.impl.combat;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.BooleanSetting;
import com.neoassist.module.setting.ModeSetting;
import com.neoassist.module.setting.NumberSetting;
import com.neoassist.util.RotationUtil;

import com.neoassist.module.impl.misc.MiddleClickFriend;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

public class KillAura extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Attack behavior",
            "Single", "Single", "Switch");
    private final NumberSetting range = new NumberSetting("Range", "Attack reach in blocks", 4.0, 2.0, 6.0, 0.1);
    private final NumberSetting delay = new NumberSetting("Delay", "Ticks between attacks", 2, 0, 20, 1);
    private final ModeSetting selector = new ModeSetting("Selector", "Target selection priority", "Distance",
            "Distance", "Health", "Angle");
    private final BooleanSetting rotate = new BooleanSetting("Rotate", "Rotate toward the target before attacking", true);
    private final NumberSetting rotateSpeed = new NumberSetting("RotateSpeed", "Max degrees per tick rotation", 45, 10, 180, 5);
    private final BooleanSetting lockTarget = new BooleanSetting("LockTarget", "Keep attacking the same valid target", true);
    private final NumberSetting fov = new NumberSetting("FOV", "Target cone in degrees; 360 targets all around", 180, 30, 360, 5);
    private final BooleanSetting targetPlayers = new BooleanSetting("Players", "Target other players", true);
    private final BooleanSetting targetMobs = new BooleanSetting("Hostiles", "Target hostile mobs", true);
    private final BooleanSetting targetAnimals = new BooleanSetting("Animals", "Target passive animals", false);
    private final BooleanSetting targetInvisible = new BooleanSetting("Invisibles", "Target invisible entities", false);
    private final BooleanSetting onlyCharged = new BooleanSetting("OnlyCharged", "Wait for full attack charge", true);
    private final BooleanSetting wallCheck = new BooleanSetting("WallCheck", "Require line of sight", true);
    private final BooleanSetting autoBlock = new BooleanSetting("AutoBlock", "Raise shield between attacks", false);

    private int ticks;
    private Entity currentTarget;
    private int switchIndex;

    public KillAura() {
        super("KillAura", "Automatically attacks nearby entities", Category.COMBAT);
        rotateSpeed.visibleWhen(() -> rotate.get());
        lockTarget.visibleWhen(() -> mode.is("Single"));
        addSettings(mode, range, delay, selector, rotate, rotateSpeed, lockTarget, fov,
                targetPlayers, targetMobs, targetAnimals, targetInvisible, onlyCharged, wallCheck, autoBlock);
    }

    public Entity getCurrentTarget() {
        return currentTarget;
    }

    @Override
    public String getInfo() {
        return mode.get();
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.gameMode == null) {
            releaseBlock();
            currentTarget = null;
            return;
        }
        if (ticks > 0) {
            ticks--;
            return;
        }
        if (onlyCharged.get() && player().getAttackStrengthScale(0.0F) < 1.0F) {
            return;
        }

        Entity target;
        if (mode.is("Switch")) {
            target = findSwitchTarget();
        } else {
            target = findTarget();
        }

        if (target == null) {
            releaseBlock();
            currentTarget = null;
            return;
        }
        currentTarget = target;

        if (rotate.get()) {
            float[] desired = RotationUtil.getRotationsToEntity(player().getEyePosition(), target);
            float maxStep = rotateSpeed.getFloat();
            float yaw = smoothRotate(player().getYRot(), desired[0], maxStep);
            float pitch = smoothRotate(player().getXRot(), desired[1], maxStep);
            player().setYRot(yaw);
            player().setXRot(pitch);
        }

        releaseBlock();
        gameMode().attack(player(), target);
        player().swing(InteractionHand.MAIN_HAND);
        ticks = delay.getInt();

        if (autoBlock.get() && holdingShield()) {
            mc.options.keyUse.setDown(true);
        }
    }

    @Override
    public void onEnable() {
        ticks = 0;
        currentTarget = null;
        switchIndex = 0;
    }

    @Override
    public void onDisable() {
        releaseBlock();
        currentTarget = null;
    }

    private boolean holdingShield() {
        return player().getOffhandItem().is(net.minecraft.world.item.Items.SHIELD)
                || player().getMainHandItem().is(net.minecraft.world.item.Items.SHIELD);
    }

    private void releaseBlock() {
        if (autoBlock.get()) {
            mc.options.keyUse.setDown(false);
        }
    }

    private float smoothRotate(float current, float target, float maxStep) {
        float delta = Mth.wrapDegrees(target - current);
        if (Math.abs(delta) <= maxStep) {
            return target;
        }
        return current + Math.signum(delta) * maxStep;
    }

    private boolean isValid(Entity entity) {
        if (!(entity instanceof LivingEntity living) || living == player() || !living.isAlive()) {
            return false;
        }
        if (MiddleClickFriend.isWhitelisted(entity)) {
            return false;
        }
        if (living.isInvisible() && !targetInvisible.get()) {
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
        if (lockTarget.get() && isSelectable(currentTarget, maxRange)) {
            return currentTarget;
        }
        Entity best = null;
        double bestScore = Double.MAX_VALUE;
        for (Entity entity : level().entitiesForRendering()) {
            if (!isSelectable(entity, maxRange)) {
                continue;
            }
            double score = score(entity);
            if (score < bestScore) {
                bestScore = score;
                best = entity;
            }
        }
        currentTarget = best;
        return best;
    }

    private Entity findSwitchTarget() {
        double maxRange = range.get();
        java.util.List<Entity> targets = new java.util.ArrayList<>();
        for (Entity entity : level().entitiesForRendering()) {
            if (isSelectable(entity, maxRange)) {
                targets.add(entity);
            }
        }
        if (targets.isEmpty()) {
            return null;
        }
        targets.sort(java.util.Comparator.comparingDouble(this::score));
        switchIndex = switchIndex % targets.size();
        Entity target = targets.get(switchIndex);
        switchIndex = (switchIndex + 1) % targets.size();
        return target;
    }

    private boolean isSelectable(Entity entity, double maxRange) {
        if (entity == null || !isValid(entity)) {
            return false;
        }
        if (player().distanceTo(entity) > maxRange) {
            return false;
        }
        if (wallCheck.get() && !player().hasLineOfSight(entity)) {
            return false;
        }
        return fov.getInt() >= 360 || angleTo(entity) <= fov.get() * 0.5;
    }

    private double score(Entity entity) {
        if (selector.is("Health") && entity instanceof LivingEntity living) {
            return living.getHealth() + living.getAbsorptionAmount();
        }
        if (selector.is("Angle")) {
            return angleTo(entity);
        }
        return player().distanceToSqr(entity);
    }

    private double angleTo(Entity entity) {
        float[] rot = RotationUtil.getRotationsToEntity(player().getEyePosition(), entity);
        double yaw = Mth.abs(Mth.wrapDegrees(rot[0] - player().getYRot()));
        double pitch = Mth.abs(Mth.wrapDegrees(rot[1] - player().getXRot()));
        return Math.hypot(yaw, pitch);
    }
}
