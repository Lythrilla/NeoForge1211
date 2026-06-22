package com.neoassist.module.impl.render;

import com.neoassist.module.Category;
import com.neoassist.module.Module;

public class NoHurtCam extends Module {
    public NoHurtCam() {
        super("NoHurtCam", "Removes the screen shake when hurt", Category.RENDER);
    }

    @Override
    public void onTick() {
        if (inGame()) {
            player().hurtTime = 0;
        }
    }
}
