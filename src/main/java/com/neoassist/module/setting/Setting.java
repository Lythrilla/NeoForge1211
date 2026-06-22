package com.neoassist.module.setting;

import java.util.function.Supplier;

import com.google.gson.JsonElement;

public abstract class Setting {
    private final String name;
    private final String description;
    private Supplier<Boolean> visibility = () -> true;

    protected Setting(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Setting visibleWhen(Supplier<Boolean> visibility) {
        this.visibility = visibility;
        return this;
    }

    public boolean isVisible() {
        return visibility.get();
    }

    public abstract JsonElement save();

    public abstract void load(JsonElement element);
}
