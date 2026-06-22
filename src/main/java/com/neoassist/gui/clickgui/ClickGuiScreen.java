package com.neoassist.gui.clickgui;

import java.util.ArrayList;
import java.util.List;

import com.neoassist.NeoAssist;
import com.neoassist.gui.GuiTheme;
import com.neoassist.module.Category;
import com.neoassist.util.RenderUtil;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClickGuiScreen extends Screen {
    private static List<Panel> panels;

    public ClickGuiScreen() {
        super(Component.literal("NeoAssist ClickGUI"));
        if (panels == null) {
            panels = new ArrayList<>();
            int x = 8;
            for (Category category : Category.values()) {
                panels.add(new Panel(category, x, 8));
                x += Panel.WIDTH + 6;
            }
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        renderBackground(g, mouseX, mouseY, partial);

        String title = "NeoAssist";
        for (Panel panel : panels) {
            panel.render(g, mouseX, mouseY, height);
        }

        // watermark
        String water = title + " \u00A77| RShift to close";
        g.drawString(font, water, 6, height - 12, GuiTheme.accent(), true);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        for (Panel panel : panels) {
            if (panel.mouseClicked(mx, my, button, height)) {
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        for (Panel panel : panels) {
            panel.mouseReleased(mx, my, button);
        }
        return super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dragX, double dragY) {
        for (Panel panel : panels) {
            panel.mouseDragged(mx, my);
        }
        return super.mouseDragged(mx, my, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
        for (Panel panel : panels) {
            panel.mouseScrolled(mx, my, scrollY, height);
        }
        return true;
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        for (Panel panel : panels) {
            if (panel.keyPressed(key, scanCode, modifiers)) {
                return true;
            }
        }
        if (key == NeoAssist.GUI_KEY) {
            onClose();
            return true;
        }
        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        super.onClose();
        NeoAssist.CONFIG.save();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
