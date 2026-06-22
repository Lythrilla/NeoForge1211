package com.neoassist.module.impl.misc;

import java.util.HashSet;
import java.util.Set;

import com.neoassist.module.Category;
import com.neoassist.module.Module;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class MiddleClickFriend extends Module {
    private static final Set<String> FRIENDS = new HashSet<>();

    public MiddleClickFriend() {
        super("MCFriend", "Middle-click players to add/remove them as friends", Category.MISC);
    }

    public static boolean isFriend(String name) {
        return FRIENDS.contains(name.toLowerCase());
    }

    public static Set<String> getFriends() {
        return FRIENDS;
    }

    @Override
    public String getInfo() {
        return FRIENDS.isEmpty() ? null : String.valueOf(FRIENDS.size());
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.screen != null) {
            return;
        }
        if (!mc.options.keyPickItem.isDown()) {
            return;
        }
        if (mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.ENTITY) {
            return;
        }
        Entity entity = ((EntityHitResult) mc.hitResult).getEntity();
        if (!(entity instanceof Player target) || target == player()) {
            return;
        }
        String name = target.getGameProfile().getName().toLowerCase();
        if (FRIENDS.remove(name)) {
            player().displayClientMessage(
                    Component.literal("\u00A7c[NeoAssist]\u00A7r Removed \u00A7e" + target.getGameProfile().getName() + "\u00A7r from friends"),
                    false);
        } else {
            FRIENDS.add(name);
            player().displayClientMessage(
                    Component.literal("\u00A7a[NeoAssist]\u00A7r Added \u00A7e" + target.getGameProfile().getName() + "\u00A7r as a friend"),
                    false);
        }
    }
}
