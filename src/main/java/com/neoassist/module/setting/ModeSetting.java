package com.neoassist.module.setting;

import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class ModeSetting extends Setting {
    private final List<String> modes;
    private int index;

    public ModeSetting(String name, String description, String defaultMode, String... modes) {
        super(name, description);
        this.modes = modes.length == 0 ? List.of(defaultMode) : Arrays.asList(modes);
        this.index = Math.max(0, this.modes.indexOf(defaultMode));
    }

    public String get() {
        return modes.get(index);
    }

    public boolean is(String mode) {
        return get().equalsIgnoreCase(mode);
    }

    public List<String> getModes() {
        return modes;
    }

    public void set(String mode) {
        int i = modes.indexOf(mode);
        if (i >= 0) {
            index = i;
        }
    }

    public void cycle() {
        index = (index + 1) % modes.size();
    }

    public void cyclePrev() {
        index = (index - 1 + modes.size()) % modes.size();
    }

    @Override
    public JsonElement save() {
        return new JsonPrimitive(get());
    }

    @Override
    public void load(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            set(element.getAsString());
        }
    }
}
