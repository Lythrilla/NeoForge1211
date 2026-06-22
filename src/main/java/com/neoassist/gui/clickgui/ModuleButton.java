package com.neoassist.gui.clickgui;

import java.util.ArrayList;
import java.util.List;

import com.neoassist.gui.GuiTheme;
import com.neoassist.gui.clickgui.component.BindComponent;
import com.neoassist.gui.clickgui.component.BlockListComponent;
import com.neoassist.gui.clickgui.component.BooleanComponent;
import com.neoassist.gui.clickgui.component.ColorComponent;
import com.neoassist.gui.clickgui.component.Component;
import com.neoassist.gui.clickgui.component.ModeComponent;
import com.neoassist.gui.clickgui.component.NumberComponent;
import com.neoassist.gui.clickgui.component.VisibilityComponent;
import com.neoassist.module.Module;
import com.neoassist.module.setting.BlockListSetting;
import com.neoassist.module.setting.BooleanSetting;
import com.neoassist.module.setting.ColorSetting;
import com.neoassist.module.setting.ModeSetting;
import com.neoassist.module.setting.NumberSetting;
import com.neoassist.module.setting.Setting;
import com.neoassist.util.RenderUtil;

import net.minecraft.client.gui.GuiGraphics;

public class ModuleButton {
    private static final int ROW_H = 14;

    private final Module module;
    private final List<Component> components = new ArrayList<>();
    private int x;
    private int y;
    private int width;

    public ModuleButton(Module module) {
        this.module = module;
        components.add(new BindComponent(module));
        components.add(new VisibilityComponent(module));
        for (Setting setting : module.getSettings()) {
            Component c = create(setting);
            if (c != null) {
                components.add(c);
            }
        }
    }

    private Component create(Setting setting) {
        if (setting instanceof BooleanSetting b) {
            return new BooleanComponent(b);
        }
        if (setting instanceof NumberSetting n) {
            return new NumberComponent(n);
        }
        if (setting instanceof ModeSetting m) {
            return new ModeComponent(m);
        }
        if (setting instanceof ColorSetting c) {
            return new ColorComponent(c);
        }
        if (setting instanceof BlockListSetting bl) {
            return new BlockListComponent(bl);
        }
        return null;
    }

    public int getTotalHeight() {
        int h = ROW_H;
        if (module.isExpanded()) {
            for (Component c : visibleComponents()) {
                h += c.getHeight();
            }
        }
        return h;
    }

    private List<Component> visibleComponents() {
        List<Component> list = new ArrayList<>();
        int i = 0;
        for (Component c : components) {
            if (i < fixedComponentCount()) {
                list.add(c);
            } else {
                Setting s = module.getSettings().get(i - fixedComponentCount());
                if (s.isVisible()) {
                    list.add(c);
                }
            }
            i++;
        }
        return list;
    }

    private int fixedComponentCount() {
        return 2;
    }

    public void setPosition(int x, int y, int width) {
        this.x = x;
        this.y = y;
        this.width = width;
    }

    public void render(GuiGraphics g, int mouseX, int mouseY) {
        boolean hover = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + ROW_H;
        int bg = hover ? GuiTheme.MODULE_BG_HOVER : GuiTheme.MODULE_BG;
        RenderUtil.rect(g, x, y, x + width, y + ROW_H, bg);
        if (module.isEnabled()) {
            RenderUtil.rect(g, x, y, x + 2, y + ROW_H, GuiTheme.accent());
        }
        int nameColor = module.isEnabled() ? GuiTheme.TEXT : GuiTheme.TEXT_DIM;
        RenderUtil.text(g, module.getName(), x + 6, y + 3, nameColor);

        String info = module.getInfo();
        if (info != null) {
            RenderUtil.text(g, info, x + width - 14 - RenderUtil.textWidth(info), y + 3, GuiTheme.accent());
        }
        if (!components.isEmpty()) {
            RenderUtil.text(g, module.isExpanded() ? "-" : "+", x + width - 8, y + 3, GuiTheme.TEXT_DIM);
        }

        if (module.isExpanded()) {
            int cy = y + ROW_H;
            for (Component c : visibleComponents()) {
                c.setBounds(x, cy, width);
                c.render(g, mouseX, mouseY);
                cy += c.getHeight();
            }
        }
    }

    public boolean mouseClicked(double mx, double my, int button) {
        boolean onHeader = mx >= x && mx <= x + width && my >= y && my <= y + ROW_H;
        if (onHeader) {
            if (button == 0) {
                module.toggle();
            } else if (button == 1) {
                module.setExpanded(!module.isExpanded());
            }
            return true;
        }
        if (module.isExpanded()) {
            for (Component c : visibleComponents()) {
                if (c.mouseClicked(mx, my, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void mouseReleased(double mx, double my, int button) {
        for (Component c : components) {
            c.mouseReleased(mx, my, button);
        }
    }

    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (module.isExpanded()) {
            for (Component c : components) {
                if (c.keyPressed(key, scanCode, modifiers)) {
                    return true;
                }
            }
        }
        return false;
    }
}
