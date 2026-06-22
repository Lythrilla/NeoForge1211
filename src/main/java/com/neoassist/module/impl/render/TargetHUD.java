package com.neoassist.module.impl.render;

import com.neoassist.NeoAssist;
import com.neoassist.gui.GuiTheme;
import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.impl.combat.KillAura;
import com.neoassist.module.setting.NumberSetting;
import com.neoassist.util.RenderUtil;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class TargetHUD extends Module {
    private final NumberSetting posX = new NumberSetting("X", "Horizontal position", 50, 0, 100, 1);
    private final NumberSetting posY = new NumberSetting("Y", "Vertical position", 40, 0, 100, 1);

    public TargetHUD() {
        super("TargetHUD", "Shows the current target's health on screen", Category.RENDER);
        addSettings(posX, posY);
    }

    @Override
    public void onRender2D(GuiGraphics g, float partial) {
        if (mc.player == null || mc.options.hideGui) {
            return;
        }
        Module ka = NeoAssist.MODULES.getByName("KillAura");
        if (ka == null || !ka.isEnabled()) {
            return;
        }
        Entity target = ((KillAura) ka).getCurrentTarget();
        if (!(target instanceof LivingEntity living) || !living.isAlive()) {
            return;
        }

        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();
        int w = 120;
        int h = 36;
        int x = (int) (sw * posX.get() / 100.0) - w / 2;
        int y = (int) (sh * posY.get() / 100.0) - h / 2;

        RenderUtil.rect(g, x, y, x + w, y + h, 0xC0111118);
        RenderUtil.outline(g, x, y, x + w, y + h, GuiTheme.PANEL_BORDER);

        String name = living.getName().getString();
        if (name.length() > 16) {
            name = name.substring(0, 16) + "..";
        }
        RenderUtil.text(g, name, x + 4, y + 4, GuiTheme.TEXT);

        float hp = living.getHealth();
        float maxHp = living.getMaxHealth();
        float absorption = living.getAbsorptionAmount();
        float ratio = Math.min(hp / maxHp, 1.0F);

        int barX = x + 4;
        int barY = y + 16;
        int barW = w - 8;
        int barH = 6;

        RenderUtil.rect(g, barX, barY, barX + barW, barY + barH, GuiTheme.SLIDER_TRACK);
        int healthColor = ratio > 0.5F ? 0xFF55FF55 : (ratio > 0.25F ? 0xFFFFAA00 : 0xFFFF5555);
        RenderUtil.rect(g, barX, barY, barX + (int) (barW * ratio), barY + barH, healthColor);

        if (absorption > 0) {
            float absRatio = Math.min(absorption / maxHp, 1.0F);
            RenderUtil.rect(g, barX, barY + barH, barX + (int) (barW * absRatio), barY + barH + 2, 0xFFFFFF00);
        }

        String hpText = String.format("%.1f / %.1f", hp, maxHp);
        RenderUtil.text(g, hpText, x + 4, y + 25, GuiTheme.TEXT_DIM);

        String distText = String.format("%.1fm", mc.player.distanceTo(living));
        int distW = RenderUtil.textWidth(distText);
        RenderUtil.text(g, distText, x + w - 4 - distW, y + 25, GuiTheme.TEXT_DIM);
    }
}
