package org.atheriumPlugin.items;

import org.bukkit.ChatColor;

public enum ItemRarity {

    COMMON(ChatColor.GREEN + "Common"),
    RARE(ChatColor.BLUE + "Rare"),
    EPIC(ChatColor.DARK_PURPLE + "Epic");

    private final String name;

    ItemRarity(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return name;
    }
}
