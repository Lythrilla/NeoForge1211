package com.neoassist.gui.clickgui.component;

import com.neoassist.gui.GuiTheme;
import com.neoassist.module.setting.ModeSetting;
import com.neoassist.util.RenderUtil;

import net.minecraft.client.gui.GuiGraphics;

public class ModeComponent extends Component {
    private final ModeSetting setting;

    public ModeComponent(ModeSetting setting) {
        this.setting = setting;
        this.height = 14;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        boolean hover = hovered(mouseX, mouseY);
        RenderUtil.rect(graphics, x, y, x + width, y + height, hover ? GuiTheme.SETTING_BG_HOVER : GuiTheme.SETTING_BG);
        RenderUtil.text(graphics, setting.getName(), x + 6, y + 3, GuiTheme.TEXT);
        String value = setting.get();
        RenderUtil.text(graphics, value, x + width - 6 - RenderUtil.textWidth(value), y + 3, GuiTheme.accent());
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (!hovered(mx, my)) {
            return false;
        }
        if (button == 0) {
            setting.cycle();
            return true;
        }
        if (button == 1) {
            setting.cyclePrev();
            return true;
        }
        return false;
    }
}
