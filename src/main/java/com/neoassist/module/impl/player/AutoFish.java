package com.neoassist.module.impl.player;

import com.neoassist.module.Category;
import com.neoassist.module.Module;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;

public class AutoFish extends Module {
    private int cooldown;
    private int recastTimer;

    public AutoFish() {
        super("AutoFish", "Reels in and recasts when a fish bites", Category.PLAYER);
    }

    private boolean holdingRod() {
        return player().getMainHandItem().is(Items.FISHING_ROD);
    }

    @Override
    public void onTick() {
        if (!inGame() || mc.screen != null || !holdingRod()) {
            return;
        }
        if (cooldown > 0) {
            cooldown--;
        }

        if (player().fishing == null) {
            // nothing cast yet (or just reeled in) -> recast after a short delay
            if (recastTimer > 0) {
                recastTimer--;
            } else if (cooldown == 0) {
                use();
                cooldown = 15;
            }
            return;
        }

        // a strong downward bob means a catch
        if (player().fishing.getDeltaMovement().y < -0.1 && cooldown == 0) {
            use(); // reel in
            cooldown = 20;
            recastTimer = 18; // recast shortly after
        }
    }

    private void use() {
        gameMode().useItem(player(), InteractionHand.MAIN_HAND);
        player().swing(InteractionHand.MAIN_HAND);
    }
}
