package com.neoassist.util;

import java.awt.Color;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;

public final class RenderUtil {
    private RenderUtil() {
    }

    public static int rgba(int r, int g, int b, int a) {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    public static int withAlpha(int argb, int alpha) {
        return (argb & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
    }

    /** Smoothly animated rainbow color. */
    public static int rainbow(int offsetMillis, float saturation, float brightness) {
        float hue = ((System.currentTimeMillis() + offsetMillis) % 4000L) / 4000.0F;
        return 0xFF000000 | (Color.HSBtoRGB(hue, saturation, brightness) & 0x00FFFFFF);
    }

    public static void rect(GuiGraphics g, double x1, double y1, double x2, double y2, int color) {
        g.fill((int) x1, (int) y1, (int) x2, (int) y2, color);
    }

    public static void outline(GuiGraphics g, double x1, double y1, double x2, double y2, int color) {
        int ix1 = (int) x1;
        int iy1 = (int) y1;
        int ix2 = (int) x2;
        int iy2 = (int) y2;
        g.fill(ix1, iy1, ix2, iy1 + 1, color);
        g.fill(ix1, iy2 - 1, ix2, iy2, color);
        g.fill(ix1, iy1, ix1 + 1, iy2, color);
        g.fill(ix2 - 1, iy1, ix2, iy2, color);
    }

    public static void text(GuiGraphics g, String s, double x, double y, int color) {
        Font font = Minecraft.getInstance().font;
        g.drawString(font, s, (int) x, (int) y, color, true);
    }

    public static void textNoShadow(GuiGraphics g, String s, double x, double y, int color) {
        Font font = Minecraft.getInstance().font;
        g.drawString(font, s, (int) x, (int) y, color, false);
    }

    public static int textWidth(String s) {
        return Minecraft.getInstance().font.width(s);
    }

    public static int fontHeight() {
        return Minecraft.getInstance().font.lineHeight;
    }

    /** Draws a semi-transparent filled box. Coordinates must already be relative to the camera. */
    public static void drawFilledBox(PoseStack poseStack, MultiBufferSource.BufferSource buffer, AABB box, int argb) {
        float a = ((argb >> 24) & 0xFF) / 255.0F * 0.4F;
        float r = ((argb >> 16) & 0xFF) / 255.0F;
        float g = ((argb >> 8) & 0xFF) / 255.0F;
        float b = (argb & 0xFF) / 255.0F;
        VertexConsumer vc = buffer.getBuffer(RenderType.debugQuads());
        var matrix = poseStack.last().pose();
        float x1 = (float) box.minX, y1 = (float) box.minY, z1 = (float) box.minZ;
        float x2 = (float) box.maxX, y2 = (float) box.maxY, z2 = (float) box.maxZ;
        // bottom
        vc.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a);
        vc.addVertex(matrix, x2, y1, z1).setColor(r, g, b, a);
        vc.addVertex(matrix, x2, y1, z2).setColor(r, g, b, a);
        vc.addVertex(matrix, x1, y1, z2).setColor(r, g, b, a);
        // top
        vc.addVertex(matrix, x1, y2, z1).setColor(r, g, b, a);
        vc.addVertex(matrix, x1, y2, z2).setColor(r, g, b, a);
        vc.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a);
        vc.addVertex(matrix, x2, y2, z1).setColor(r, g, b, a);
        // north (-Z)
        vc.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a);
        vc.addVertex(matrix, x1, y2, z1).setColor(r, g, b, a);
        vc.addVertex(matrix, x2, y2, z1).setColor(r, g, b, a);
        vc.addVertex(matrix, x2, y1, z1).setColor(r, g, b, a);
        // south (+Z)
        vc.addVertex(matrix, x1, y1, z2).setColor(r, g, b, a);
        vc.addVertex(matrix, x2, y1, z2).setColor(r, g, b, a);
        vc.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a);
        vc.addVertex(matrix, x1, y2, z2).setColor(r, g, b, a);
        // west (-X)
        vc.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a);
        vc.addVertex(matrix, x1, y1, z2).setColor(r, g, b, a);
        vc.addVertex(matrix, x1, y2, z2).setColor(r, g, b, a);
        vc.addVertex(matrix, x1, y2, z1).setColor(r, g, b, a);
        // east (+X)
        vc.addVertex(matrix, x2, y1, z1).setColor(r, g, b, a);
        vc.addVertex(matrix, x2, y2, z1).setColor(r, g, b, a);
        vc.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a);
        vc.addVertex(matrix, x2, y1, z2).setColor(r, g, b, a);
    }

    /** Draws a wireframe box. Coordinates must already be relative to the camera. */
    public static void drawBox(PoseStack poseStack, MultiBufferSource.BufferSource buffer, AABB box, int argb) {
        VertexConsumer vc = buffer.getBuffer(RenderType.lines());
        float a = ((argb >> 24) & 0xFF) / 255.0F;
        float r = ((argb >> 16) & 0xFF) / 255.0F;
        float g = ((argb >> 8) & 0xFF) / 255.0F;
        float b = (argb & 0xFF) / 255.0F;
        LevelRenderer.renderLineBox(poseStack, vc, box, r, g, b, a);
    }

    /** Draws a single line between two camera-relative points. */
    public static void drawLine(PoseStack poseStack, MultiBufferSource.BufferSource buffer,
            double x1, double y1, double z1, double x2, double y2, double z2, int argb) {
        VertexConsumer vc = buffer.getBuffer(RenderType.lines());
        float a = ((argb >> 24) & 0xFF) / 255.0F;
        float r = ((argb >> 16) & 0xFF) / 255.0F;
        float g = ((argb >> 8) & 0xFF) / 255.0F;
        float b = (argb & 0xFF) / 255.0F;
        var matrix = poseStack.last();
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
        float nx = (float) (dx / len);
        float ny = (float) (dy / len);
        float nz = (float) (dz / len);
        vc.addVertex(matrix.pose(), (float) x1, (float) y1, (float) z1).setColor(r, g, b, a).setNormal(matrix, nx, ny, nz);
        vc.addVertex(matrix.pose(), (float) x2, (float) y2, (float) z2).setColor(r, g, b, a).setNormal(matrix, nx, ny, nz);
    }
}
