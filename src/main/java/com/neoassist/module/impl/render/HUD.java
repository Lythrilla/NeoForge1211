package com.neoassist.module.impl.render;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.neoassist.NeoAssist;
import com.neoassist.gui.GuiTheme;
import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.BooleanSetting;
import com.neoassist.util.RenderUtil;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;

public class HUD extends Module {
    private final BooleanSetting watermark = new BooleanSetting("Watermark", "Show the NeoAssist logo", true);
    private final BooleanSetting arrayList = new BooleanSetting("ArrayList", "Show enabled modules", true);
    private final BooleanSetting coords = new BooleanSetting("Coordinates", "Show your position", true);
    private final BooleanSetting fps = new BooleanSetting("FPS", "Show frames per second", true);
    private final BooleanSetting direction = new BooleanSetting("Direction", "Show facing direction", true);
    private final BooleanSetting ping = new BooleanSetting("Ping", "Show server latency", true);

    public HUD() {
        super("HUD", "On-screen overlay (ArrayList, coords, FPS, ping)", Category.RENDER);
        addSettings(watermark, arrayList, coords, fps, direction, ping);
    }

    @Override
    public void onRender2D(GuiGraphics g, float partial) {
        if (mc.options.hideGui || mc.player == null) {
            return;
        }
        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();

        if (watermark.get()) {
            RenderUtil.text(g, "NeoAssist", 3, 3, GuiTheme.accent());
        }

        if (arrayList.get()) {
            renderArrayList(g, sw);
        }

        int bottom = sh - 11;
        if (coords.get()) {
            String pos = String.format("XYZ: %.0f, %.0f, %.0f",
                    mc.player.getX(), mc.player.getY(), mc.player.getZ());
            RenderUtil.text(g, pos, 3, bottom, GuiTheme.TEXT);
            bottom -= 10;
        }
        if (direction.get()) {
            RenderUtil.text(g, "Facing: " + mc.player.getDirection().getName(), 3, bottom, GuiTheme.TEXT);
            bottom -= 10;
        }
        if (ping.get() && mc.getConnection() != null) {
            PlayerInfo info = mc.getConnection().getPlayerInfo(mc.player.getUUID());
            if (info != null) {
                RenderUtil.text(g, info.getLatency() + " ms", 3, bottom, GuiTheme.TEXT);
                bottom -= 10;
            }
        }
        if (fps.get()) {
            RenderUtil.text(g, mc.getFps() + " FPS", 3, bottom, GuiTheme.TEXT);
        }
    }

    private void renderArrayList(GuiGraphics g, int sw) {
        List<Module> enabled = new ArrayList<>();
        for (Module m : NeoAssist.MODULES.getEnabled()) {
            if (m.isVisible()) {
                enabled.add(m);
            }
        }
        enabled.sort(Comparator.comparingInt((Module m) -> RenderUtil.textWidth(label(m))).reversed());

        int y = 2;
        int i = 0;
        for (Module m : enabled) {
            String text = label(m);
            int w = RenderUtil.textWidth(text);
            int x = sw - w - 3;
            RenderUtil.rect(g, x - 2, y, sw, y + 10, 0x80000000);
            RenderUtil.text(g, text, x, y + 1, GuiTheme.accent(i * 200));
            y += 10;
            i++;
        }
    }

    private String label(Module m) {
        String info = m.getInfo();
        return info == null ? m.getName() : m.getName() + " " + info;
    }
}
