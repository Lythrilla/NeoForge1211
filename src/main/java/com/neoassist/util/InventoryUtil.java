package com.neoassist.util;

import java.util.Set;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

public final class InventoryUtil {
    private InventoryUtil() {
    }

    private static final Set<Item> BAD_FOOD = Set.of(
            Items.ROTTEN_FLESH,
            Items.SPIDER_EYE,
            Items.POISONOUS_POTATO,
            Items.PUFFERFISH,
            Items.CHICKEN,
            Items.SUSPICIOUS_STEW,
            Items.CHORUS_FRUIT);

    public static boolean isEdible(ItemStack stack) {
        return !stack.isEmpty() && stack.has(DataComponents.FOOD) && !BAD_FOOD.contains(stack.getItem());
    }

    /** Returns a hotbar slot (0-8) holding safe food, or -1. */
    public static int findFoodHotbarSlot(Player player) {
        for (int i = 0; i < 9; i++) {
            if (isEdible(player.getInventory().getItem(i))) {
                return i;
            }
        }
        return -1;
    }

    /** Returns the hotbar slot (0-8) with the fastest tool for the given block, or -1. */
    public static int findBestToolHotbarSlot(Player player, BlockState state) {
        int best = -1;
        float bestSpeed = 1.0F;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            float speed = stack.getDestroySpeed(state);
            if (speed > bestSpeed) {
                bestSpeed = speed;
                best = i;
            }
        }
        return best;
    }
}
