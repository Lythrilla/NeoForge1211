package com.neoassist.module.impl.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.ColorSetting;
import com.neoassist.util.RenderUtil;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class Trajectories extends Module {
    private final ColorSetting color = new ColorSetting("Color", "Path color", 0xC03CDCFF);

    public Trajectories() {
        super("Trajectories", "Predicts the flight path of projectiles you hold", Category.RENDER);
        addSettings(color);
    }

    @Override
    public void onWorldRender(PoseStack poseStack, MultiBufferSource.BufferSource buffer, Vec3 cameraPos, float partial) {
        if (!inGame()) {
            return;
        }
        ItemStack stack = player().getMainHandItem();
        Item item = stack.getItem();

        double speed;
        double gravity;
        if (player().isUsingItem() && player().getUseItem().getItem() instanceof BowItem) {
            int charge = player().getUseItem().getUseDuration(player()) - player().getUseItemRemainingTicks();
            float power = BowItem.getPowerForTime(charge);
            if (power <= 0.1F) {
                return;
            }
            speed = power * 3.0;
            gravity = 0.05;
        } else if (item instanceof CrossbowItem) {
            speed = 3.15;
            gravity = 0.05;
        } else if (item instanceof TridentItem) {
            speed = 2.5;
            gravity = 0.05;
        } else if (item instanceof SnowballItem || item instanceof EggItem || item instanceof EnderpearlItem) {
            speed = 1.5;
            gravity = 0.03;
        } else {
            return;
        }

        simulateAndDraw(poseStack, buffer, cameraPos, partial, speed, gravity);
    }

    private void simulateAndDraw(PoseStack poseStack, MultiBufferSource.BufferSource buffer, Vec3 cameraPos,
            float partial, double speed, double gravity) {
        float yaw = player().getViewYRot(partial);
        float pitch = player().getViewXRot(partial);
        double radYaw = Math.toRadians(yaw);
        double radPitch = Math.toRadians(pitch);

        double dx = -Math.sin(radYaw) * Math.cos(radPitch);
        double dy = -Math.sin(radPitch);
        double dz = Math.cos(radYaw) * Math.cos(radPitch);

        Vec3 pos = player().getEyePosition(partial).subtract(0, 0.1, 0);
        Vec3 vel = new Vec3(dx, dy, dz).normalize().scale(speed);

        int argb = color.get();
        for (int i = 0; i < 200; i++) {
            Vec3 next = pos.add(vel);
            BlockHitResult hit = level().clip(new ClipContext(pos, next,
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player()));
            boolean stop = hit.getType() != HitResult.Type.MISS;
            Vec3 end = stop ? hit.getLocation() : next;

            RenderUtil.drawLine(poseStack, buffer,
                    pos.x - cameraPos.x, pos.y - cameraPos.y, pos.z - cameraPos.z,
                    end.x - cameraPos.x, end.y - cameraPos.y, end.z - cameraPos.z, argb);

            if (stop) {
                AABB marker = new AABB(end.x - 0.15, end.y - 0.15, end.z - 0.15,
                        end.x + 0.15, end.y + 0.15, end.z + 0.15)
                        .move(-cameraPos.x, -cameraPos.y, -cameraPos.z);
                RenderUtil.drawBox(poseStack, buffer, marker, argb);
                break;
            }

            pos = next;
            vel = vel.scale(0.99).subtract(0, gravity, 0);
            if (pos.y < level().getMinBuildHeight()) {
                break;
            }
        }
    }
}
