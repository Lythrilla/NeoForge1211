package com.neoassist.gui.clickgui.component;

import com.neoassist.NeoAssist;

import net.minecraft.client.gui.GuiGraphics;

public abstract class Component {
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    public void setBounds(int x, int y, int width) {
        this.x = x;
        this.y = y;
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    protected boolean hovered(double mx, double my) {
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }

    protected void requestSave() {
        if (NeoAssist.CONFIG != null) {
            NeoAssist.CONFIG.requestSave();
        }
    }

    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY);

    public boolean mouseClicked(double mx, double my, int button) {
        return false;
    }

    public void mouseReleased(double mx, double my, int button) {
    }

    public boolean keyPressed(int key, int scanCode, int modifiers) {
        return false;
    }
}
