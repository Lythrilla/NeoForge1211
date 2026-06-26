package com.neoassist.module.setting;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

/**
 * A set of selected entity types (by registry id), edited through the in-GUI entity selector.
 * Shared between KillAura (skip attacking) and the ESP modules (skip highlighting).
 */
public class EntityTypeListSetting extends Setting {
    private final Set<String> ids = new LinkedHashSet<>();
    /** Lazily resolved {@code id -> EntityType} view; rebuilt only when {@link #ids} changes. */
    private Set<EntityType<?>> resolved;

    public EntityTypeListSetting(String name, String description, String... defaults) {
        super(name, description);
        for (String id : defaults) {
            ids.add(id);
        }
    }

    public Set<String> getIds() {
        return ids;
    }

    public int size() {
        return ids.size();
    }

    public boolean contains(EntityType<?> type) {
        return resolved().contains(type);
    }

    public boolean contains(Entity entity) {
        return entity != null && contains(entity.getType());
    }

    /**
     * Resolves the selected ids to {@link EntityType} instances once and caches the result so the
     * per-entity membership test in hot loops (KillAura targeting, ESP rendering) avoids the
     * registry-key lookup + string allocation on every call.
     */
    private Set<EntityType<?>> resolved() {
        Set<EntityType<?>> cache = resolved;
        if (cache == null) {
            cache = new HashSet<>();
            for (String id : ids) {
                ResourceLocation key = ResourceLocation.tryParse(id);
                if (key != null && BuiltInRegistries.ENTITY_TYPE.containsKey(key)) {
                    cache.add(BuiltInRegistries.ENTITY_TYPE.get(key));
                }
            }
            resolved = cache;
        }
        return cache;
    }

    public void toggle(EntityType<?> type) {
        String key = BuiltInRegistries.ENTITY_TYPE.getKey(type).toString();
        if (!ids.remove(key)) {
            ids.add(key);
        }
        resolved = null;
    }

    public void add(EntityType<?> type) {
        ids.add(BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
        resolved = null;
    }

    public void remove(EntityType<?> type) {
        ids.remove(BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
        resolved = null;
    }

    public boolean containsId(String id) {
        return ids.contains(id);
    }

    public void clear() {
        ids.clear();
        resolved = null;
    }

    @Override
    public JsonElement save() {
        JsonArray array = new JsonArray();
        for (String id : ids) {
            array.add(new JsonPrimitive(id));
        }
        return array;
    }

    @Override
    public void load(JsonElement element) {
        if (element != null && element.isJsonArray()) {
            Set<String> loaded = new LinkedHashSet<>();
            for (JsonElement e : element.getAsJsonArray()) {
                if (!e.isJsonPrimitive()) {
                    continue;
                }
                ResourceLocation id = ResourceLocation.tryParse(e.getAsString());
                if (id != null && BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
                    loaded.add(id.toString());
                }
            }
            ids.clear();
            ids.addAll(loaded);
            resolved = null;
        }
    }
}
