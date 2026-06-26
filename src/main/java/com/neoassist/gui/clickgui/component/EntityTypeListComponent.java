package com.neoassist.gui.clickgui.component;

import com.neoassist.gui.GuiTheme;
import com.neoassist.gui.clickgui.EntitySelectScreen;
import com.neoassist.module.setting.EntityTypeListSetting;
import com.neoassist.util.RenderUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class EntityTypeListComponent extends Component {
    private final EntityTypeListSetting setting;

    public EntityTypeListComponent(EntityTypeListSetting setting) {
        this.setting = setting;
        this.height = 14;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        boolean hover = hovered(mouseX, mouseY);
        RenderUtil.rect(graphics, x, y, x + width, y + height, hover ? GuiTheme.SETTING_BG_HOVER : GuiTheme.SETTING_BG);
        RenderUtil.text(graphics, setting.getName(), x + 6, y + 3, GuiTheme.TEXT);
        String label = "Edit (" + setting.size() + ")";
        RenderUtil.text(graphics, label, x + width - 6 - RenderUtil.textWidth(label), y + 3, GuiTheme.accent());
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0 && hovered(mx, my)) {
            Minecraft mc = Minecraft.getInstance();
            mc.setScreen(new EntitySelectScreen(setting, mc.screen));
            return true;
        }
        return false;
    }
}
