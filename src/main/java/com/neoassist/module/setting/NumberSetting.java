package com.neoassist.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class NumberSetting extends Setting {
    private double value;
    private final double min;
    private final double max;
    private final double step;

    public NumberSetting(String name, String description, double defaultValue, double min, double max, double step) {
        super(name, description);
        this.value = defaultValue;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public double get() {
        return value;
    }

    public float getFloat() {
        return (float) value;
    }

    public int getInt() {
        return (int) Math.round(value);
    }

    public void set(double value) {
        double clamped = Math.max(min, Math.min(max, value));
        if (step > 0) {
            clamped = Math.round(clamped / step) * step;
        }
        this.value = clamped;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public boolean isInteger() {
        return step >= 1.0 && step == Math.floor(step);
    }

    @Override
    public JsonElement save() {
        return new JsonPrimitive(value);
    }

    @Override
    public void load(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            set(element.getAsDouble());
        }
    }
}
