package com.neoassist.gui.clickgui.component;

import com.neoassist.gui.GuiTheme;
import com.neoassist.module.setting.ColorSetting;
import com.neoassist.util.RenderUtil;

import net.minecraft.client.gui.GuiGraphics;

public class ColorComponent extends Component {
    private final ColorSetting setting;
    private boolean open;
    private int draggingChannel = -1; // 0=A,1=R,2=G,3=B

    private static final String[] LABELS = { "Alpha", "Red", "Green", "Blue" };
    private static final int ROW = 12;

    public ColorComponent(ColorSetting setting) {
        this.setting = setting;
        this.height = 14;
    }

    @Override
    public int getHeight() {
        return open ? 14 + LABELS.length * ROW : 14;
    }

    private int channelValue(int ch) {
        return switch (ch) {
            case 0 -> setting.getAlpha();
            case 1 -> setting.getRed();
            case 2 -> setting.getGreen();
            default -> setting.getBlue();
        };
    }

    private void setChannel(int ch, int value) {
        int a = setting.getAlpha();
        int r = setting.getRed();
        int g = setting.getGreen();
        int b = setting.getBlue();
        switch (ch) {
            case 0 -> a = value;
            case 1 -> r = value;
            case 2 -> g = value;
            default -> b = value;
        }
        setting.setComponents(a, r, g, b);
    }

    private int trackLeft() {
        return x + 6;
    }

    private int trackRight() {
        return x + width - 6;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        boolean hoverHeader = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 14;
        RenderUtil.rect(graphics, x, y, x + width, y + 14, hoverHeader ? GuiTheme.SETTING_BG_HOVER : GuiTheme.SETTING_BG);
        RenderUtil.text(graphics, setting.getName(), x + 6, y + 3, GuiTheme.TEXT);
        int sw = 18;
        int sx = x + width - sw - 6;
        RenderUtil.rect(graphics, sx, y + 3, sx + sw, y + 11, setting.get());
        RenderUtil.outline(graphics, sx, y + 3, sx + sw, y + 11, GuiTheme.TEXT_DIM);

        if (!open) {
            return;
        }

        if (draggingChannel >= 0) {
            int tl = trackLeft();
            int tr = trackRight();
            double pct = Math.max(0, Math.min(1, (mouseX - tl) / (double) (tr - tl)));
            setChannel(draggingChannel, (int) Math.round(pct * 255));
        }

        for (int ch = 0; ch < LABELS.length; ch++) {
            int rowY = y + 14 + ch * ROW;
            RenderUtil.rect(graphics, x, rowY, x + width, rowY + ROW, GuiTheme.SETTING_BG);
            RenderUtil.text(graphics, LABELS[ch], x + 8, rowY + 1, GuiTheme.TEXT_DIM);
            String v = String.valueOf(channelValue(ch));
            RenderUtil.text(graphics, v, trackRight() - RenderUtil.textWidth(v), rowY + 1, GuiTheme.TEXT_DIM);

            int tl = trackLeft();
            int tr = trackRight();
            int ty = rowY + ROW - 3;
            RenderUtil.rect(graphics, tl, ty, tr, ty + 2, GuiTheme.SLIDER_TRACK);
            int fillX = (int) (tl + (tr - tl) * (channelValue(ch) / 255.0));
            RenderUtil.rect(graphics, tl, ty, fillX, ty + 2, GuiTheme.accent(ch * 400));
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (mx >= x && mx <= x + width && my >= y && my <= y + 14) {
            if (button == 0) {
                open = !open;
                return true;
            }
        }
        if (open && button == 0) {
            for (int ch = 0; ch < LABELS.length; ch++) {
                int rowY = y + 14 + ch * ROW;
                if (my >= rowY && my <= rowY + ROW && mx >= x && mx <= x + width) {
                    draggingChannel = ch;
                    int tl = trackLeft();
                    int tr = trackRight();
                    double pct = Math.max(0, Math.min(1, (mx - tl) / (double) (tr - tl)));
                    setChannel(ch, (int) Math.round(pct * 255));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void mouseReleased(double mx, double my, int button) {
        draggingChannel = -1;
    }
}
