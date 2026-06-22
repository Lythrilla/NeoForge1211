package com.neoassist.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neoassist.NeoAssist;
import com.neoassist.gui.GuiTheme;
import com.neoassist.module.Module;
import com.neoassist.module.setting.Setting;

import net.minecraft.client.Minecraft;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int SAVE_DELAY_TICKS = 20;

    private final File configFile;
    private boolean dirty;
    private int saveDelay;

    public ConfigManager() {
        File dir = new File(Minecraft.getInstance().gameDirectory, "neoassist");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.configFile = new File(dir, "config.json");
    }

    public void save() {
        JsonObject root = new JsonObject();
        root.addProperty("version", 1);
        root.addProperty("rainbow", GuiTheme.rainbow);

        JsonObject modulesObj = new JsonObject();
        for (Module module : NeoAssist.MODULES.getModules()) {
            JsonObject mod = new JsonObject();
            mod.addProperty("enabled", module.isEnabled());
            mod.addProperty("key", module.getKey());
            mod.addProperty("visible", module.isVisible());
            mod.addProperty("expanded", module.isExpanded());
            JsonObject settings = new JsonObject();
            for (Setting setting : module.getSettings()) {
                settings.add(setting.getName(), setting.save());
            }
            mod.add("settings", settings);
            modulesObj.add(module.getName(), mod);
        }
        root.add("modules", modulesObj);

        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(root, writer);
            dirty = false;
            saveDelay = 0;
        } catch (Exception e) {
            NeoAssist.LOGGER.error("Failed to save config", e);
        }
    }

    public void requestSave() {
        dirty = true;
        saveDelay = SAVE_DELAY_TICKS;
    }

    public void tick() {
        if (!dirty) {
            return;
        }
        if (saveDelay > 0) {
            saveDelay--;
            return;
        }
        save();
    }

    public void flushIfDirty() {
        if (dirty) {
            save();
        }
    }

    public void load() {
        if (!Files.exists(configFile.toPath())) {
            return;
        }
        try (FileReader reader = new FileReader(configFile)) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            if (root == null) {
                return;
            }
            if (root.has("rainbow") && root.get("rainbow").isJsonPrimitive()) {
                GuiTheme.rainbow = root.get("rainbow").getAsBoolean();
            }
            if (!root.has("modules") || !root.get("modules").isJsonObject()) {
                return;
            }
            JsonObject modulesObj = root.getAsJsonObject("modules");
            for (Module module : NeoAssist.MODULES.getModules()) {
                try {
                    if (!modulesObj.has(module.getName()) || !modulesObj.get(module.getName()).isJsonObject()) {
                        continue;
                    }
                    JsonObject mod = modulesObj.getAsJsonObject(module.getName());
                    if (mod.has("key") && mod.get("key").isJsonPrimitive()) {
                        module.setKey(mod.get("key").getAsInt());
                    }
                    if (mod.has("visible") && mod.get("visible").isJsonPrimitive()) {
                        module.setVisible(mod.get("visible").getAsBoolean());
                    }
                    if (mod.has("expanded") && mod.get("expanded").isJsonPrimitive()) {
                        module.setExpanded(mod.get("expanded").getAsBoolean());
                    }
                    if (mod.has("settings") && mod.get("settings").isJsonObject()) {
                        JsonObject settings = mod.getAsJsonObject("settings");
                        for (Setting setting : module.getSettings()) {
                            if (settings.has(setting.getName())) {
                                JsonElement el = settings.get(setting.getName());
                                try {
                                    setting.load(el);
                                } catch (Exception e) {
                                    NeoAssist.LOGGER.warn("Skipping invalid config for {}.{}", module.getName(),
                                            setting.getName(), e);
                                }
                            }
                        }
                    }
                    if (mod.has("enabled") && mod.get("enabled").isJsonPrimitive()) {
                        module.setEnabled(mod.get("enabled").getAsBoolean());
                    }
                } catch (Exception e) {
                    NeoAssist.LOGGER.warn("Skipping invalid config for module {}", module.getName(), e);
                }
            }
            dirty = false;
            saveDelay = 0;
        } catch (Exception e) {
            NeoAssist.LOGGER.error("Failed to load config", e);
        }
    }
}
