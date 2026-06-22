package com.neoassist.module;

public enum Category {
    COMBAT("Combat"),
    PLAYER("Player"),
    MOVEMENT("Movement"),
    WORLD("World"),
    RENDER("Render"),
    MISC("Misc");

    public final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }
}
