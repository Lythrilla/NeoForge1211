package com.neoassist.module.impl.misc;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.neoassist.module.Category;
import com.neoassist.module.Module;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class MiddleClickFriend extends Module {
    private static final Set<String> FRIENDS = new HashSet<>();
    private static final Set<UUID> WHITELIST = new HashSet<>();

    private boolean wasDown;

    public MiddleClickFriend() {
        super("MCFriend", "Middle-click entities to whitelist them from KillAura", Category.MISC);
    }

    public static boolean isFriend(String name) {
        return FRIENDS.contains(name.toLowerCase());
    }

    public static boolean isWhitelisted(Entity entity) {
        if (entity instanceof Player p) {
            return FRIENDS.contains(p.getGameProfile().getName().toLowerCase());
        }
        return WHITELIST.contains(entity.getUUID());
    }

    public static Set<String> getFriends() {
        return FRIENDS;
    }

    @Override
    public String getInfo() {
        int count = FRIENDS.size() + WHITELIST.size();
        return count == 0 ? null : String.valueOf(count);
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.screen != null) {
            wasDown = false;
            return;
        }
        boolean down = mc.options.keyPickItem.isDown();
        if (!down || wasDown) {
            wasDown = down;
            return;
        }
        wasDown = true;

        if (mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.ENTITY) {
            return;
        }
        Entity entity = ((EntityHitResult) mc.hitResult).getEntity();
        if (entity == player()) {
            return;
        }

        if (entity instanceof Player target) {
            String name = target.getGameProfile().getName().toLowerCase();
            String displayName = target.getGameProfile().getName();
            if (FRIENDS.remove(name)) {
                player().displayClientMessage(
                        Component.literal("\u00A7c[NeoAssist]\u00A7r Removed \u00A7e" + displayName + "\u00A7r from whitelist"),
                        false);
            } else {
                FRIENDS.add(name);
                player().displayClientMessage(
                        Component.literal("\u00A7a[NeoAssist]\u00A7r Added \u00A7e" + displayName + "\u00A7r to whitelist"),
                        false);
            }
        } else {
            UUID uuid = entity.getUUID();
            String displayName = entity.getName().getString();
            if (WHITELIST.remove(uuid)) {
                player().displayClientMessage(
                        Component.literal("\u00A7c[NeoAssist]\u00A7r Removed \u00A7e" + displayName + "\u00A7r from whitelist"),
                        false);
            } else {
                WHITELIST.add(uuid);
                player().displayClientMessage(
                        Component.literal("\u00A7a[NeoAssist]\u00A7r Added \u00A7e" + displayName + "\u00A7r to whitelist"),
                        false);
            }
        }
    }
}
