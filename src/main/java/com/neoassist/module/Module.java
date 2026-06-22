package com.neoassist.module;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neoassist.NeoAssist;
import com.neoassist.module.setting.Setting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import org.lwjgl.glfw.GLFW;

public abstract class Module {
    protected final Minecraft mc = Minecraft.getInstance();

    private final String name;
    private final String description;
    private final Category category;
    private final List<Setting> settings = new ArrayList<>();

    private boolean enabled;
    private int key = GLFW.GLFW_KEY_UNKNOWN;
    private boolean visible = true;
    private boolean expanded;

    protected Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    protected void addSettings(Setting... toAdd) {
        for (Setting s : toAdd) {
            settings.add(s);
        }
    }

    public List<Setting> getSettings() {
        return settings;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    /** Optional short text shown next to the name in the ArrayList (e.g. current mode). */
    public String getInfo() {
        return null;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean state) {
        if (state == enabled) {
            return;
        }
        this.enabled = state;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
        requestSave();
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        if (this.key == key) {
            return;
        }
        this.key = key;
        requestSave();
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (this.visible == visible) {
            return;
        }
        this.visible = visible;
        requestSave();
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        if (this.expanded == expanded) {
            return;
        }
        this.expanded = expanded;
        requestSave();
    }

    private void requestSave() {
        if (NeoAssist.CONFIG != null) {
            NeoAssist.CONFIG.requestSave();
        }
    }

    // ---- lifecycle hooks ----
    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onTick() {
    }

    public void onRender2D(net.minecraft.client.gui.GuiGraphics graphics, float partial) {
    }

    public void onWorldRender(PoseStack poseStack, MultiBufferSource.BufferSource buffer, Vec3 cameraPos, float partial) {
    }

    public void onFov(ComputeFovModifierEvent event) {
    }

    public void onAttack(net.minecraft.world.entity.Entity target) {
    }

    // ---- convenience accessors (null-safe checks left to callers) ----
    protected LocalPlayer player() {
        return mc.player;
    }

    protected ClientLevel level() {
        return mc.level;
    }

    protected MultiPlayerGameMode gameMode() {
        return mc.gameMode;
    }

    protected boolean inGame() {
        return mc.player != null && mc.level != null;
    }
}
