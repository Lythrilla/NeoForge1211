package com.neoassist.module.impl.movement;

import com.neoassist.module.Category;
import com.neoassist.module.Module;
import com.neoassist.module.setting.ModeSetting;

public class AutoSprint extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Sprint behavior",
            "Legit", "Legit", "Omni");

    public AutoSprint() {
        super("AutoSprint", "Always sprints while moving", Category.MOVEMENT);
        addSettings(mode);
    }

    @Override
    public String getInfo() {
        return mode.get();
    }

    @Override
    public void onTick() {
        if (!inGame()) {
            return;
        }
        if (mode.is("Legit")) {
            if (player().zza > 0 && !player().isShiftKeyDown() && player().getFoodData().getFoodLevel() > 6) {
                player().setSprinting(true);
            }
        } else if (mode.is("Omni")) {
            if ((player().zza != 0 || player().xxa != 0) && !player().isShiftKeyDown()
                    && player().getFoodData().getFoodLevel() > 6) {
                player().setSprinting(true);
            }
        }
    }
}
