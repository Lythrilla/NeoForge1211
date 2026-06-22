package com.neoassist.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class BooleanSetting extends Setting {
    private boolean value;

    public BooleanSetting(String name, String description, boolean defaultValue) {
        super(name, description);
        this.value = defaultValue;
    }

    public boolean get() {
        return value;
    }

    public void set(boolean value) {
        this.value = value;
    }

    public void toggle() {
        this.value = !this.value;
    }

    @Override
    public JsonElement save() {
        return new JsonPrimitive(value);
    }

    @Override
    public void load(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            this.value = element.getAsBoolean();
        }
    }
}
