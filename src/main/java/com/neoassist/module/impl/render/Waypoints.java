package com.neoassist.module.impl.render;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.BooleanSetting;
import com.neoassist.module.setting.ColorSetting;
import com.neoassist.util.RenderUtil;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Waypoints extends Module {
    private final BooleanSetting showDistance = new BooleanSetting("ShowDistance", "Show distance on HUD", true);
    private final BooleanSetting deathPoint = new BooleanSetting("DeathPoint", "Auto-mark death position", true);
    private final ColorSetting color = new ColorSetting("Color", "Waypoint beam color", 0xC0FF4444);

    private final List<int[]> points = new ArrayList<>();

    public Waypoints() {
        super("Waypoints", "Mark positions with visible beams (sneak+rightclick to add)", Category.RENDER);
        addSettings(showDistance, deathPoint, color);
    }

    public void addWaypoint(int x, int y, int z) {
        for (int[] p : points) {
            if (p[0] == x && p[1] == y && p[2] == z) {
                return;
            }
        }
        points.add(new int[] { x, y, z });
    }

    public void addDeathPoint(int x, int y, int z) {
        if (deathPoint.get()) {
            addWaypoint(x, y, z);
        }
    }

    public void clearWaypoints() {
        points.clear();
    }

    @Override
    public String getInfo() {
        return points.isEmpty() ? null : String.valueOf(points.size());
    }

    @Override
    public void onWorldRender(PoseStack poseStack, MultiBufferSource.BufferSource buffer, Vec3 cameraPos, float partial) {
        if (!inGame() || points.isEmpty()) {
            return;
        }
        int argb = color.get();
        for (int[] p : points) {
            double x = p[0] + 0.5 - cameraPos.x;
            double z = p[2] + 0.5 - cameraPos.z;
            double y1 = p[1] - cameraPos.y;
            double y2 = 320 - cameraPos.y;
            AABB beam = new AABB(x - 0.1, y1, z - 0.1, x + 0.1, y2, z + 0.1);
            RenderUtil.drawFilledBox(poseStack, buffer, beam, argb);
            AABB marker = new AABB(x - 0.5, y1, z - 0.5, x + 0.5, y1 + 1, z + 0.5);
            RenderUtil.drawBox(poseStack, buffer, marker, argb);
        }
    }

    @Override
    public void onRender2D(GuiGraphics g, float partial) {
        if (!inGame() || !showDistance.get() || points.isEmpty()) {
            return;
        }
        int sw = mc.getWindow().getGuiScaledWidth();
        int y = 2;
        for (int[] p : points) {
            double dist = Math.sqrt(player().distanceToSqr(p[0] + 0.5, p[1] + 0.5, p[2] + 0.5));
            String text = String.format("[WP] %d, %d, %d (%.0fm)", p[0], p[1], p[2], dist);
            int w = RenderUtil.textWidth(text);
            RenderUtil.text(g, text, (sw - w) / 2.0, y, color.get() | 0xFF000000);
            y += 10;
        }
    }
}
