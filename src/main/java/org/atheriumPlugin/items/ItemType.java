package org.atheriumPlugin.items;


import org.bukkit.inventory.EquipmentSlot;

import static org.bukkit.inventory.EquipmentSlot.*;

public enum ItemType {

    AXE("Axe", HAND),
    SWORD("Sword", HAND),
    BOW("Bow", HAND),
    OFFHAND("Off Hand", OFF_HAND),
    GENERIC("Item", HAND),
    BOOTS("Boots", FEET),
    LEGGINGS("Leggings", LEGS),
    CHESTPLATE("Chestplate", CHEST),
    HELMET("Helmet", HEAD);

    private final String name;
    private final EquipmentSlot slot;

    ItemType(String name, EquipmentSlot slot) {
        this.name = name;
        this.slot = slot;
    }

    public String getDisplayName() {
        return name;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }
}
