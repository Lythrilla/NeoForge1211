package com.neoassist.module.impl.movement;

import com.neoassist.module.Category;
import com.neoassist.module.Module;

public class NoSlow extends Module {
    /** Read by LocalPlayerMixin to decide whether to cancel item-use slowdown. */
    public static volatile boolean active = false;

    public NoSlow() {
        super("NoSlow", "Removes the movement slowdown while eating/blocking/using items", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        active = true;
    }

    @Override
    public void onDisable() {
        active = false;
    }
}
