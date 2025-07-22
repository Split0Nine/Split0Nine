package org.lootboxes;

import org.bukkit.ChatColor;

public enum LootType {
    YELLOW(4, ChatColor.YELLOW + "Yellow Loot"),
    GREEN(5, ChatColor.GREEN + "Green Loot"),
    BLACK(15, ChatColor.BLACK + "Black Loot");

    private final int data;
    private final String displayName;

    LootType(int data, String displayName) {
        this.data = data;
        this.displayName = displayName;
    }

    public int getData() {
        return data;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return getColor();
    }
}