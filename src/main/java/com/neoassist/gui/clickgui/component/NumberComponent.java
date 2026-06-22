package com.neoassist.gui.clickgui.component;

import com.neoassist.gui.GuiTheme;
import com.neoassist.module.setting.NumberSetting;
import com.neoassist.util.RenderUtil;

import net.minecraft.client.gui.GuiGraphics;

public class NumberComponent extends Component {
    private final NumberSetting setting;
    private boolean dragging;

    public NumberComponent(NumberSetting setting) {
        this.setting = setting;
        this.height = 22;
    }

    private int trackTop() {
        return y + 14;
    }

    private int trackLeft() {
        return x + 6;
    }

    private int trackRight() {
        return x + width - 6;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        boolean hover = hovered(mouseX, mouseY);
        RenderUtil.rect(graphics, x, y, x + width, y + height, hover ? GuiTheme.SETTING_BG_HOVER : GuiTheme.SETTING_BG);

        if (dragging) {
            updateFromMouse(mouseX);
        }

        String valueText = setting.isInteger()
                ? String.valueOf(setting.getInt())
                : String.format("%.2f", setting.get());
        RenderUtil.text(graphics, setting.getName(), x + 6, y + 3, GuiTheme.TEXT);
        RenderUtil.text(graphics, valueText, trackRight() - RenderUtil.textWidth(valueText), y + 3, GuiTheme.TEXT_DIM);

        int tl = trackLeft();
        int tr = trackRight();
        int ty = trackTop();
        RenderUtil.rect(graphics, tl, ty, tr, ty + 3, GuiTheme.SLIDER_TRACK);

        double pct = (setting.get() - setting.getMin()) / (setting.getMax() - setting.getMin());
        pct = Math.max(0, Math.min(1, pct));
        int fillX = (int) (tl + (tr - tl) * pct);
        RenderUtil.rect(graphics, tl, ty, fillX, ty + 3, GuiTheme.accent());
        RenderUtil.rect(graphics, fillX - 1, ty - 2, fillX + 2, ty + 5, GuiTheme.TEXT);
    }

    private void updateFromMouse(double mx) {
        int tl = trackLeft();
        int tr = trackRight();
        double pct = (mx - tl) / (double) (tr - tl);
        pct = Math.max(0, Math.min(1, pct));
        double value = setting.getMin() + (setting.getMax() - setting.getMin()) * pct;
        setting.set(value);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0 && hovered(mx, my)) {
            dragging = true;
            updateFromMouse(mx);
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(double mx, double my, int button) {
        dragging = false;
    }
}
