package com.neoassist.module.impl.render;

import com.neoassist.module.Category;
import com.neoassist.module.Module;

public class NoHurtCam extends Module {
    /** Snapshot of the real hurtTime before this module zeroes it out. */
    public static volatile int realHurtTime;

    public NoHurtCam() {
        super("NoHurtCam", "Removes the screen shake when hurt", Category.RENDER);
    }

    @Override
    public void onTick() {
        if (inGame()) {
            realHurtTime = player().hurtTime;
            player().hurtTime = 0;
        }
    }
}
