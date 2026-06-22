package com.neoassist.module.impl.movement;

import com.neoassist.module.Category;
import com.neoassist.module.Module;

public class AutoSprint extends Module {
    public AutoSprint() {
        super("AutoSprint", "Always sprints while moving forward", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (!inGame()) {
            return;
        }
        if (player().zza > 0 && !player().isShiftKeyDown() && player().getFoodData().getFoodLevel() > 6) {
            player().setSprinting(true);
        }
    }
}
