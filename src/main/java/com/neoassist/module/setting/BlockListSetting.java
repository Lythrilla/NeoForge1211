package com.neoassist.module.setting;

import java.util.HashSet;
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
    /** Lazily resolved {@code id -> Block} view; rebuilt only when {@link #ids} changes. */
    private Set<Block> resolved;

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
        return resolved().contains(block);
    }

    /**
     * Resolves the selected ids to {@link Block} instances once and caches the result. Membership
     * tests then avoid the per-call registry key lookup + string allocation that made hot scan
     * loops (e.g. Block ESP) expensive over large volumes.
     */
    private Set<Block> resolved() {
        Set<Block> cache = resolved;
        if (cache == null) {
            cache = new HashSet<>();
            for (String id : ids) {
                ResourceLocation key = ResourceLocation.tryParse(id);
                if (key == null) {
                    continue;
                }
                Block block = BuiltInRegistries.BLOCK.get(key);
                if (block != Blocks.AIR) {
                    cache.add(block);
                }
            }
            resolved = cache;
        }
        return cache;
    }

    public boolean contains(BlockState state) {
        return contains(state.getBlock());
    }

    public void toggle(Block block) {
        String key = BuiltInRegistries.BLOCK.getKey(block).toString();
        if (!ids.remove(key)) {
            ids.add(key);
        }
        resolved = null;
    }

    public void add(Block block) {
        ids.add(BuiltInRegistries.BLOCK.getKey(block).toString());
        resolved = null;
    }

    public void remove(Block block) {
        ids.remove(BuiltInRegistries.BLOCK.getKey(block).toString());
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
            resolved = null;
        }
    }
}
