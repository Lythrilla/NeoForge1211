package com.neoassist.gui.clickgui;

import java.util.ArrayList;
import java.util.List;

import com.neoassist.NeoAssist;
import com.neoassist.gui.GuiTheme;
import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.util.RenderUtil;

import net.minecraft.client.gui.GuiGraphics;

public class Panel {
    public static final int WIDTH = 116;
    private static final int HEADER_H = 16;

    private final Category category;
    private final List<ModuleButton> buttons = new ArrayList<>();

    private int x;
    private int y;
    private boolean open = true;
    private boolean dragging;
    private int dragOffsetX;
    private int dragOffsetY;
    private double scroll;

    public Panel(Category category, int x, int y) {
        this.category = category;
        this.x = x;
        this.y = y;
        for (Module module : NeoAssist.MODULES.getModules(category)) {
            buttons.add(new ModuleButton(module));
        }
    }

    private int contentHeight() {
        int h = 0;
        for (ModuleButton b : buttons) {
            h += b.getTotalHeight();
        }
        return h;
    }

    private int maxVisible(int screenHeight) {
        return Math.max(40, screenHeight - (y + HEADER_H) - 8);
    }

    private int visibleHeight(int screenHeight) {
        return Math.min(contentHeight(), maxVisible(screenHeight));
    }

    private double maxScroll(int screenHeight) {
        return Math.max(0, contentHeight() - maxVisible(screenHeight));
    }

    public void render(GuiGraphics g, int mouseX, int mouseY, int screenHeight) {
        // header
        RenderUtil.rect(g, x, y, x + WIDTH, y + HEADER_H, GuiTheme.accent());
        RenderUtil.text(g, category.displayName, x + 6, y + 4, 0xFFFFFFFF);
        RenderUtil.text(g, open ? "-" : "+", x + WIDTH - 9, y + 4, 0xFFFFFFFF);

        if (!open) {
            return;
        }

        int top = y + HEADER_H;
        int visible = visibleHeight(screenHeight);
        int bottom = top + visible;
        RenderUtil.rect(g, x, top, x + WIDTH, bottom, GuiTheme.PANEL_BG);

        scroll = Math.max(0, Math.min(maxScroll(screenHeight), scroll));

        g.enableScissor(x, top, x + WIDTH, bottom);
        int cy = top - (int) scroll;
        for (ModuleButton b : buttons) {
            b.setPosition(x, cy, WIDTH);
            b.render(g, mouseX, mouseY);
            cy += b.getTotalHeight();
        }
        g.disableScissor();

        RenderUtil.outline(g, x, y, x + WIDTH, bottom, GuiTheme.PANEL_BORDER);
    }

    private boolean onHeader(double mx, double my) {
        return mx >= x && mx <= x + WIDTH && my >= y && my <= y + HEADER_H;
    }

    public boolean mouseClicked(double mx, double my, int button, int screenHeight) {
        if (onHeader(mx, my)) {
            if (button == 0) {
                dragging = true;
                dragOffsetX = (int) (mx - x);
                dragOffsetY = (int) (my - y);
            } else if (button == 1) {
                open = !open;
            }
            return true;
        }
        if (!open) {
            return false;
        }
        int top = y + HEADER_H;
        int bottom = top + visibleHeight(screenHeight);
        if (mx >= x && mx <= x + WIDTH && my >= top && my <= bottom) {
            for (ModuleButton b : buttons) {
                if (b.mouseClicked(mx, my, button)) {
                    return true;
                }
            }
            return true; // consume clicks inside panel body
        }
        return false;
    }

    public void mouseReleased(double mx, double my, int button) {
        dragging = false;
        for (ModuleButton b : buttons) {
            b.mouseReleased(mx, my, button);
        }
    }

    public void mouseDragged(double mx, double my) {
        if (dragging) {
            x = (int) (mx - dragOffsetX);
            y = (int) (my - dragOffsetY);
        }
    }

    public void mouseScrolled(double mx, double my, double scrollY, int screenHeight) {
        if (open && mx >= x && mx <= x + WIDTH && my >= y && my <= y + HEADER_H + visibleHeight(screenHeight)) {
            scroll = Math.max(0, Math.min(maxScroll(screenHeight), scroll - scrollY * 14));
        }
    }

    public boolean keyPressed(int key, int scanCode, int modifiers) {
        for (ModuleButton b : buttons) {
            if (b.keyPressed(key, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }
}
