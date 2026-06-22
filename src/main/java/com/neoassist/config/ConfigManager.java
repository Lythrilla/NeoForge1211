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

    private final File configFile;

    public ConfigManager() {
        File dir = new File(Minecraft.getInstance().gameDirectory, "neoassist");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.configFile = new File(dir, "config.json");
    }

    public void save() {
        JsonObject root = new JsonObject();
        root.addProperty("rainbow", GuiTheme.rainbow);

        JsonObject modulesObj = new JsonObject();
        for (Module module : NeoAssist.MODULES.getModules()) {
            JsonObject mod = new JsonObject();
            mod.addProperty("enabled", module.isEnabled());
            mod.addProperty("key", module.getKey());
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
        } catch (Exception e) {
            NeoAssist.LOGGER.error("Failed to save config", e);
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
            if (root.has("rainbow")) {
                GuiTheme.rainbow = root.get("rainbow").getAsBoolean();
            }
            if (!root.has("modules")) {
                return;
            }
            JsonObject modulesObj = root.getAsJsonObject("modules");
            for (Module module : NeoAssist.MODULES.getModules()) {
                if (!modulesObj.has(module.getName())) {
                    continue;
                }
                JsonObject mod = modulesObj.getAsJsonObject(module.getName());
                if (mod.has("key")) {
                    module.setKey(mod.get("key").getAsInt());
                }
                if (mod.has("settings")) {
                    JsonObject settings = mod.getAsJsonObject("settings");
                    for (Setting setting : module.getSettings()) {
                        if (settings.has(setting.getName())) {
                            JsonElement el = settings.get(setting.getName());
                            setting.load(el);
                        }
                    }
                }
                if (mod.has("enabled")) {
                    module.setEnabled(mod.get("enabled").getAsBoolean());
                }
            }
        } catch (Exception e) {
            NeoAssist.LOGGER.error("Failed to load config", e);
        }
    }
}
