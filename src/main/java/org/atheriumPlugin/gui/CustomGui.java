package org.atheriumPlugin.gui;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

public abstract class CustomGui implements InventoryHolder {

    private final Inventory inventory;

    public CustomGui(String title, int rows) {
        inventory = Bukkit.createInventory(this, rows * 9, title);
    }

    public abstract void onClick(InventoryClickEvent event);

    @Override
    @MonotonicNonNull
    public Inventory getInventory() {
        return inventory;
    }
}
