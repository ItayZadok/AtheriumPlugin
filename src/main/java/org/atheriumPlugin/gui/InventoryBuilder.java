package org.atheriumPlugin.gui;

import org.atheriumPlugin.utility.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryBuilder {

    private final Inventory inventory;

    public InventoryBuilder(int rows, String title) {
        inventory = new CustomGui(title, rows) {

            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                player.openInventory(inventory);
                if (event.getSlot() == inventory.getSize() - 1) // exit button
                    player.closeInventory();
            }
        }.getInventory();
    }

    public InventoryBuilder basicMenu(Material material) {
        fillAll(new ItemBuilder(material).setDisplayName(" ").build());
        inventory.setItem(inventory.getSize() - 1,
                new ItemBuilder(Material.BARRIER).setDisplayName(ChatColor.RED + "Exit").build());
        return this;
    }

    public InventoryBuilder fillBorder(ItemStack itemStack) {
        int size = inventory.getSize();
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, itemStack);  // Top row
            inventory.setItem(size - 9 + i, itemStack);  // Bottom row
        }
        for (int i = 9; i < size - 9; i += 9) {
            inventory.setItem(i, itemStack);  // Left column
            inventory.setItem(i + 8, itemStack);  // Right column
        }
        return this;
    }

    public InventoryBuilder setItem(int slot, ItemStack itemStack) {
        inventory.setItem(slot, itemStack);
        return this;
    }

    public InventoryBuilder fillRow(int row, ItemStack itemStack) {
        int start = row * 9;
        for (int i = start; i < start + 9; i++) {
            inventory.setItem(i, itemStack);
        }
        return this;
    }

    public InventoryBuilder fillColumn(int column, ItemStack itemStack) {
        for (int i = column; i < inventory.getSize(); i += 9) {
            inventory.setItem(i, itemStack);
        }
        return this;
    }

    public InventoryBuilder fillAll(ItemStack itemStack) {
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, itemStack);
        }
        return this;
    }

    public InventoryBuilder addItem(ItemStack itemStack) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, itemStack);
                break;
            }
        }
        return this;
    }

    public InventoryBuilder clear() {
        inventory.clear();
        return this;
    }

    public Inventory buildInventory() {
        return inventory;
    }
}


