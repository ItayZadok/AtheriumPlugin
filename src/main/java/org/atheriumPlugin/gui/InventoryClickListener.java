package org.atheriumPlugin.gui;

import org.atheriumPlugin.gui.inventories.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void on(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory() == null ? event.getInventory() : event.getClickedInventory();
        if (inventory.getHolder() == null) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(inventory.getHolder() instanceof CustomGui customGui)) return;
        customGui.onClick(event);

        if (ClassSelectionMenu.inventories.contains(inventory)) {
            ClassSelectionMenu.handleInventory(player, event.getSlot());
        }
        else if (AbilitySelectionMenu.inventories.contains(inventory)) {
            AbilitySelectionMenu.handleInventory(player, event.getSlot(), event.getClick());
        }
        else if (CustomItemsMenu.inventories.contains(inventory)) {
            CustomItemsMenu.handleInventory(player, event.getSlot());
        }
        else if (AbilityInfoMenu.inventories.containsKey(inventory)) {
            AbilityInfoMenu.handleInventory(player, event.getSlot());
        }
    }
}
