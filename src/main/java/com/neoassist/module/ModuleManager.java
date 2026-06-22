package com.neoassist.module;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neoassist.NeoAssist;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public void register(Module... toAdd) {
        for (Module m : toAdd) {
            modules.add(m);
        }
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> getModules(Category category) {
        List<Module> list = new ArrayList<>();
        for (Module m : modules) {
            if (m.getCategory() == category) {
                list.add(m);
            }
        }
        return list;
    }

    public Module getByName(String name) {
        for (Module m : modules) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }

    public List<Module> getEnabled() {
        List<Module> list = new ArrayList<>();
        for (Module m : modules) {
            if (m.isEnabled()) {
                list.add(m);
            }
        }
        return list;
    }

    public void onKey(int key) {
        for (Module m : modules) {
            if (m.getKey() == key && key != org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN) {
                m.toggle();
            }
        }
    }

    public void onTick() {
        for (Module m : modules) {
            if (m.isEnabled()) {
                try {
                    m.onTick();
                } catch (Exception e) {
                    NeoAssist.LOGGER.error("Error ticking module {}", m.getName(), e);
                }
            }
        }
    }

    public void onRender2D(GuiGraphics graphics, float partial) {
        for (Module m : modules) {
            if (m.isEnabled()) {
                m.onRender2D(graphics, partial);
            }
        }
    }

    public void onWorldRender(PoseStack poseStack, MultiBufferSource.BufferSource buffer, Vec3 cameraPos, float partial) {
        for (Module m : modules) {
            if (m.isEnabled()) {
                m.onWorldRender(poseStack, buffer, cameraPos, partial);
            }
        }
    }

    public void onFov(ComputeFovModifierEvent event) {
        for (Module m : modules) {
            if (m.isEnabled()) {
                m.onFov(event);
            }
        }
    }

    public void onAttack(net.minecraft.world.entity.Entity target) {
        for (Module m : modules) {
            if (m.isEnabled()) {
                m.onAttack(target);
            }
        }
    }
}
