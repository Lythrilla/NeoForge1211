package com.neoassist.gui.clickgui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.neoassist.NeoAssist;
import com.neoassist.gui.GuiTheme;
import com.neoassist.module.setting.EntityTypeListSetting;
import com.neoassist.util.RenderUtil;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/** Searchable entity-type picker used to populate an {@link EntityTypeListSetting} (KillAura/ESP whitelist). */
public class EntitySelectScreen extends Screen {
    /**
     * Immutable, pre-computed view of a registry entity type. Building this once avoids resolving
     * translatable names and registry keys on every keystroke/frame.
     */
    private static final class Entry {
        final EntityType<?> type;
        final String id;
        final String name;
        final String searchText;

        Entry(EntityType<?> type, String id, String name) {
            this.type = type;
            this.id = id;
            this.name = name;
            this.searchText = (id + ' ' + name).toLowerCase(Locale.ROOT);
        }
    }

    /** Registry snapshot shared across all picker instances; built lazily on first open. */
    private static List<Entry> registryCache;

    private final EntityTypeListSetting setting;
    private final Screen parent;

    private EditBox search;
    private final List<Entry> shown = new ArrayList<>();
    private final String[] queryTokens = new String[8];
    private int tokenCount;
    private boolean selectedOnly;
    private double scroll;

    private static final int ROW_H = 18;
    private static final int GAP = 4;
    private static final int BUTTON_H = 16;
    private static final int LIST_HALF = 130;

    public EntitySelectScreen(EntityTypeListSetting setting, Screen parent) {
        super(Component.literal("Select Entities"));
        this.setting = setting;
        this.parent = parent;
    }

    private int listLeft() {
        return width / 2 - LIST_HALF;
    }

    private int listRight() {
        return width / 2 + LIST_HALF;
    }

    private int listTop() {
        return 72;
    }

    private int listBottom() {
        return height - 24;
    }

    private int buttonsTop() {
        return 50;
    }

    private int buttonWidth() {
        return (LIST_HALF * 2 - GAP * 3) / 4;
    }

    private int buttonX(int index) {
        return listLeft() + index * (buttonWidth() + GAP);
    }

    private boolean onButton(double mx, double my, int index) {
        int left = buttonX(index);
        return mx >= left && mx <= left + buttonWidth() && my >= buttonsTop() && my <= buttonsTop() + BUTTON_H;
    }

    private void requestSave() {
        if (NeoAssist.CONFIG != null) {
            NeoAssist.CONFIG.requestSave();
        }
    }

    private static List<Entry> registry() {
        if (registryCache == null) {
            List<Entry> list = new ArrayList<>();
            for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
                // Only living/spawnable creatures make sense as a "species" whitelist entry.
                if (type.getCategory() == MobCategory.MISC) {
                    continue;
                }
                String id = BuiltInRegistries.ENTITY_TYPE.getKey(type).toString();
                String name = type.getDescription().getString();
                list.add(new Entry(type, id, name));
            }
            list.sort((a, b) -> a.name.compareToIgnoreCase(b.name));
            registryCache = list;
        }
        return registryCache;
    }

    @Override
    protected void init() {
        registry();
        search = new EditBox(font, listLeft(), 28, LIST_HALF * 2, 18, Component.literal("Search"));
        search.setMaxLength(64);
        search.setHint(Component.literal("Search by name or mod id (e.g. minecraft:)"));
        search.setResponder(s -> refilter());
        addRenderableWidget(search);
        setInitialFocus(search);
        refilter();
    }

    private void refilter() {
        tokenCount = 0;
        if (search != null) {
            String q = search.getValue().toLowerCase(Locale.ROOT).trim();
            if (!q.isEmpty()) {
                for (String token : q.split("\\s+")) {
                    if (!token.isEmpty() && tokenCount < queryTokens.length) {
                        queryTokens[tokenCount++] = token;
                    }
                }
            }
        }
        shown.clear();
        for (Entry entry : registry()) {
            if (selectedOnly && !setting.containsId(entry.id)) {
                continue;
            }
            if (matches(entry)) {
                shown.add(entry);
            }
        }
        clampScroll();
    }

    private boolean matches(Entry entry) {
        for (int i = 0; i < tokenCount; i++) {
            if (!entry.searchText.contains(queryTokens[i])) {
                return false;
            }
        }
        return true;
    }

    private void clampScroll() {
        scroll = Math.max(0, Math.min(maxScroll(), scroll));
    }

    private int maxScroll() {
        int visible = listBottom() - listTop();
        return Math.max(0, shown.size() * ROW_H - visible);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        renderBackground(g, mouseX, mouseY, partial);
        RenderUtil.rect(g, listLeft() - 4, 4, listRight() + 4, height - 4, GuiTheme.PANEL_BG);
        RenderUtil.outline(g, listLeft() - 4, 4, listRight() + 4, height - 4, GuiTheme.accent());

        super.render(g, mouseX, mouseY, partial);

        g.drawString(font, "Select Entities  (" + setting.size() + " selected, " + shown.size() + " shown)",
                listLeft(), 14, GuiTheme.TEXT, true);

        drawButton(g, 0, "Add shown", mouseX, mouseY, false);
        drawButton(g, 1, "Remove", mouseX, mouseY, false);
        drawButton(g, 2, "Selected", mouseX, mouseY, selectedOnly);
        drawButton(g, 3, "Clear", mouseX, mouseY, false);

        int top = listTop();
        int bottom = listBottom();
        g.enableScissor(listLeft(), top, listRight(), bottom);
        int first = Math.max(0, (int) (scroll / ROW_H));
        int last = Math.min(shown.size(), (int) ((scroll + (bottom - top)) / ROW_H) + 1);
        for (int i = first; i < last; i++) {
            Entry entry = shown.get(i);
            int y = top - (int) scroll + i * ROW_H;
            boolean hover = mouseX >= listLeft() && mouseX <= listRight() && mouseY >= y && mouseY <= y + ROW_H;
            boolean selected = setting.containsId(entry.id);
            int bg = selected ? RenderUtil.withAlpha(GuiTheme.accent(), 90)
                    : (hover ? GuiTheme.MODULE_BG_HOVER : GuiTheme.MODULE_BG);
            RenderUtil.rect(g, listLeft(), y, listRight(), y + ROW_H, bg);
            g.drawString(font, entry.name, listLeft() + 6, y + 5, GuiTheme.TEXT, false);
            if (selected) {
                g.drawString(font, "\u2713", listRight() - 14, y + 5, GuiTheme.accent(), false);
            }
        }
        g.disableScissor();

        if (shown.isEmpty()) {
            String empty = selectedOnly ? "No selected entities match the search" : "No entities match the search";
            g.drawString(font, empty, listLeft(), top + 6, GuiTheme.TEXT_DIM, false);
        }

        g.drawString(font, "Click an entity to toggle  |  Add/Remove act on shown results  |  ESC to go back",
                listLeft(), height - 16, GuiTheme.TEXT_DIM, false);
    }

    private void drawButton(GuiGraphics g, int index, String label, int mouseX, int mouseY, boolean active) {
        int left = buttonX(index);
        int top = buttonsTop();
        int w = buttonWidth();
        boolean hover = mouseX >= left && mouseX <= left + w && mouseY >= top && mouseY <= top + BUTTON_H;
        int bg = active ? RenderUtil.withAlpha(GuiTheme.accent(), 90)
                : (hover ? GuiTheme.MODULE_BG_HOVER : GuiTheme.MODULE_BG);
        RenderUtil.rect(g, left, top, left + w, top + BUTTON_H, bg);
        RenderUtil.outline(g, left, top, left + w, top + BUTTON_H, GuiTheme.PANEL_BORDER);
        g.drawString(font, label, left + (w - font.width(label)) / 2, top + 4, GuiTheme.TEXT, false);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0 && onButton(mx, my, 0)) {
            for (Entry entry : shown) {
                setting.add(entry.type);
            }
            requestSave();
            if (selectedOnly) {
                refilter();
            }
            return true;
        }
        if (button == 0 && onButton(mx, my, 1)) {
            for (Entry entry : shown) {
                setting.remove(entry.type);
            }
            requestSave();
            refilter();
            return true;
        }
        if (button == 0 && onButton(mx, my, 2)) {
            selectedOnly = !selectedOnly;
            scroll = 0;
            refilter();
            return true;
        }
        if (button == 0 && onButton(mx, my, 3)) {
            setting.clear();
            requestSave();
            if (selectedOnly) {
                refilter();
            }
            return true;
        }
        if (button == 0 && mx >= listLeft() && mx <= listRight() && my >= listTop() && my <= listBottom()) {
            int index = (int) ((my - listTop() + scroll) / ROW_H);
            if (index >= 0 && index < shown.size()) {
                Entry entry = shown.get(index);
                setting.toggle(entry.type);
                requestSave();
                if (selectedOnly) {
                    refilter();
                }
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
