package com.neoassist.module.setting;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A set of selected blocks (by registry id), edited through the in-GUI block selector.
 */
public class BlockListSetting extends Setting {
    private final Set<String> ids = new LinkedHashSet<>();

    public BlockListSetting(String name, String description, String... defaults) {
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

    public boolean contains(Block block) {
        ResourceLocation key = BuiltInRegistries.BLOCK.getKey(block);
        return ids.contains(key.toString());
    }

    public boolean contains(BlockState state) {
        return contains(state.getBlock());
    }

    public void toggle(Block block) {
        String key = BuiltInRegistries.BLOCK.getKey(block).toString();
        if (!ids.remove(key)) {
            ids.add(key);
        }
    }

    public void add(Block block) {
        ids.add(BuiltInRegistries.BLOCK.getKey(block).toString());
    }

    public void clear() {
        ids.clear();
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
            ids.clear();
            for (JsonElement e : element.getAsJsonArray()) {
                if (!e.isJsonPrimitive()) {
                    continue;
                }
                ResourceLocation id = ResourceLocation.tryParse(e.getAsString());
                if (id != null && BuiltInRegistries.BLOCK.get(id) != Blocks.AIR) {
                    loaded.add(id.toString());
                }
            }
            ids.addAll(loaded);
        }
    }
}
