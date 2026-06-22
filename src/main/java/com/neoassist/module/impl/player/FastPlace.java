package com.neoassist.module.impl.player;

import com.neoassist.module.Category;
import com.neoassist.module.Module;

public class FastPlace extends Module {
    public FastPlace() {
        super("FastPlace", "Removes the delay between block/item right-clicks", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (inGame()) {
            mc.rightClickDelay = 0;
        }
    }
}
