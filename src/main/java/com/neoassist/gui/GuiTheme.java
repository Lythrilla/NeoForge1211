package com.neoassist.gui;

import com.neoassist.util.RenderUtil;

/** Central place for ClickGUI colors so the look stays consistent. */
public final class GuiTheme {
    private GuiTheme() {
    }

    public static boolean rainbow = true;

    public static final int PANEL_BG = 0xE6111118;
    public static final int PANEL_BORDER = 0xFF26263A;
    public static final int MODULE_BG = 0xFF181826;
    public static final int MODULE_BG_HOVER = 0xFF22223A;
    public static final int SETTING_BG = 0xFF14141F;
    public static final int SETTING_BG_HOVER = 0xFF1C1C2C;
    public static final int TEXT = 0xFFE6E6F0;
    public static final int TEXT_DIM = 0xFF8C8CA6;
    public static final int SLIDER_TRACK = 0xFF2A2A40;

    private static final int ACCENT = 0xFF6C5CE7;

    public static int accent() {
        return rainbow ? RenderUtil.rainbow(0, 0.55F, 1.0F) : ACCENT;
    }

    public static int accent(int offset) {
        return rainbow ? RenderUtil.rainbow(offset, 0.55F, 1.0F) : ACCENT;
    }
}
