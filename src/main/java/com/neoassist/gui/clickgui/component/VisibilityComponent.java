package com.neoassist.gui.clickgui.component;

import com.neoassist.gui.GuiTheme;
import com.neoassist.module.Module;
import com.neoassist.util.RenderUtil;

import net.minecraft.client.gui.GuiGraphics;

public class VisibilityComponent extends Component {
    private final Module module;

    public VisibilityComponent(Module module) {
        this.module = module;
        this.height = 14;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        boolean hover = hovered(mouseX, mouseY);
        RenderUtil.rect(graphics, x, y, x + width, y + height,
                hover ? GuiTheme.SETTING_BG_HOVER : GuiTheme.SETTING_BG);
        RenderUtil.text(graphics, "Show in HUD", x + 6, y + 3, GuiTheme.TEXT);

        int boxSize = 7;
        int bx = x + width - boxSize - 6;
        int by = y + (height - boxSize) / 2;
        if (module.isVisible()) {
            RenderUtil.rect(graphics, bx, by, bx + boxSize, by + boxSize, GuiTheme.accent());
        } else {
            RenderUtil.outline(graphics, bx, by, bx + boxSize, by + boxSize, GuiTheme.TEXT_DIM);
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0 && hovered(mx, my)) {
            module.setVisible(!module.isVisible());
            return true;
        }
        return false;
    }
}
