package com.neoassist.gui.clickgui.component;

import com.neoassist.gui.GuiTheme;
import com.neoassist.module.Module;
import com.neoassist.util.RenderUtil;

import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

/** Lets the user bind a key to toggle the owning module. */
public class BindComponent extends Component {
    private final Module module;
    private boolean listening;

    public BindComponent(Module module) {
        this.module = module;
        this.height = 14;
    }

    private String keyName() {
        int key = module.getKey();
        if (key == GLFW.GLFW_KEY_UNKNOWN) {
            return "None";
        }
        String name = GLFW.glfwGetKeyName(key, 0);
        if (name != null) {
            return name.toUpperCase();
        }
        return switch (key) {
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "RSHIFT";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "LSHIFT";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "RCTRL";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "LCTRL";
            case GLFW.GLFW_KEY_LEFT_ALT -> "LALT";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "RALT";
            default -> "KEY " + key;
        };
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        boolean hover = hovered(mouseX, mouseY);
        RenderUtil.rect(graphics, x, y, x + width, y + height, hover ? GuiTheme.SETTING_BG_HOVER : GuiTheme.SETTING_BG);
        RenderUtil.text(graphics, "Bind", x + 6, y + 3, GuiTheme.TEXT);
        String value = listening ? "..." : keyName();
        RenderUtil.text(graphics, value, x + width - 6 - RenderUtil.textWidth(value), y + 3,
                listening ? GuiTheme.accent() : GuiTheme.TEXT_DIM);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (hovered(mx, my)) {
            listening = !listening;
            return true;
        }
        if (listening) {
            listening = false;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (!listening) {
            return false;
        }
        if (key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_DELETE || key == GLFW.GLFW_KEY_BACKSPACE) {
            module.setKey(GLFW.GLFW_KEY_UNKNOWN);
        } else {
            module.setKey(key);
        }
        requestSave();
        listening = false;
        return true;
    }
}
