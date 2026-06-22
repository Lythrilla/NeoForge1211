package com.neoassist.gui.clickgui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.neoassist.gui.GuiTheme;
import com.neoassist.module.setting.BlockListSetting;
import com.neoassist.util.RenderUtil;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/** Searchable block picker used to populate a {@link BlockListSetting} (e.g. Block ESP targets). */
public class BlockSelectScreen extends Screen {
    private final BlockListSetting setting;
    private final Screen parent;

    private EditBox search;
    private final List<Block> all = new ArrayList<>();
    private final List<Block> filtered = new ArrayList<>();
    private double scroll;

    private static final int ROW_H = 18;

    public BlockSelectScreen(BlockListSetting setting, Screen parent) {
        super(Component.literal("Select Blocks"));
        this.setting = setting;
        this.parent = parent;
    }

    private int listLeft() {
        return width / 2 - 130;
    }

    private int listRight() {
        return width / 2 + 130;
    }

    private int listTop() {
        return 56;
    }

    private int listBottom() {
        return height - 24;
    }

    @Override
    protected void init() {
        all.clear();
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block != Blocks.AIR && block != Blocks.CAVE_AIR && block != Blocks.VOID_AIR) {
                all.add(block);
            }
        }
        search = new EditBox(font, width / 2 - 130, 30, 260, 18, Component.literal("Search"));
        search.setMaxLength(64);
        search.setResponder(s -> refilter());
        addRenderableWidget(search);
        setInitialFocus(search);
        refilter();
    }

    private void refilter() {
        String q = search == null ? "" : search.getValue().toLowerCase(Locale.ROOT).trim();
        filtered.clear();
        for (Block block : all) {
            String id = BuiltInRegistries.BLOCK.getKey(block).getPath();
            String name = block.getName().getString().toLowerCase(Locale.ROOT);
            if (q.isEmpty() || id.contains(q) || name.contains(q)) {
                filtered.add(block);
            }
        }
        scroll = 0;
    }

    private int maxScroll() {
        int visible = listBottom() - listTop();
        return Math.max(0, filtered.size() * ROW_H - visible);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        renderBackground(g, mouseX, mouseY, partial);
        RenderUtil.rect(g, listLeft() - 4, 4, listRight() + 4, height - 4, GuiTheme.PANEL_BG);
        RenderUtil.outline(g, listLeft() - 4, 4, listRight() + 4, height - 4, GuiTheme.accent());

        super.render(g, mouseX, mouseY, partial);

        g.drawString(font, "Select Blocks  (" + setting.size() + " selected)", listLeft(), 14, GuiTheme.TEXT, true);

        int top = listTop();
        int bottom = listBottom();
        g.enableScissor(listLeft(), top, listRight(), bottom);
        int y = top - (int) scroll;
        for (Block block : filtered) {
            if (y + ROW_H >= top && y <= bottom) {
                boolean hover = mouseX >= listLeft() && mouseX <= listRight() && mouseY >= y && mouseY <= y + ROW_H;
                boolean selected = setting.contains(block);
                int bg = selected ? RenderUtil.withAlpha(GuiTheme.accent(), 90) : (hover ? GuiTheme.MODULE_BG_HOVER : GuiTheme.MODULE_BG);
                RenderUtil.rect(g, listLeft(), y, listRight(), y + ROW_H, bg);
                g.renderItem(new ItemStack(block), listLeft() + 2, y + 1);
                String name = block.getName().getString();
                g.drawString(font, name, listLeft() + 22, y + 5, GuiTheme.TEXT, false);
                if (selected) {
                    String tick = "\u2713";
                    g.drawString(font, tick, listRight() - 14, y + 5, GuiTheme.accent(), false);
                }
            }
            y += ROW_H;
        }
        g.disableScissor();

        g.drawString(font, "Click a block to toggle  |  ESC to go back", listLeft(), height - 16, GuiTheme.TEXT_DIM, false);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0 && mx >= listLeft() && mx <= listRight() && my >= listTop() && my <= listBottom()) {
            int index = (int) ((my - listTop() + scroll) / ROW_H);
            if (index >= 0 && index < filtered.size()) {
                setting.toggle(filtered.get(index));
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
        scroll = Math.max(0, Math.min(maxScroll(), scroll - scrollY * ROW_H * 2));
        return true;
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
