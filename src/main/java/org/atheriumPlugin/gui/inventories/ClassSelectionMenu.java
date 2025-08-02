package org.atheriumPlugin.gui.inventories;

import org.atheriumPlugin.classes.ClassType;
import org.atheriumPlugin.gui.InventoryBuilder;
import org.atheriumPlugin.player.PlayerAbilityManager;
import org.atheriumPlugin.player.PlayerSystem;
import org.atheriumPlugin.utility.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClassSelectionMenu {

    public static final List<Inventory> inventories = new ArrayList<>();
    private static final HashMap<ClassType, Integer> classSlots = new HashMap<>();
    private static final int RESET_SLOT = 18;

    public static Inventory getInventory(Player player) {
        InventoryBuilder inventoryBuilder = new InventoryBuilder(3, "Class Selection")
                .basicMenu(Material.GRAY_STAINED_GLASS_PANE);
        ClassType playerClass = PlayerSystem.getInstance().getPlayerProfile(player).getAbilityManager().getClassType();
        boolean hasClass = !playerClass.equals(ClassType.DEFAULT);
        inventoryBuilder.setItem(RESET_SLOT, new ItemBuilder(Material.RED_TERRACOTTA)
                .setDisplayName(ChatColor.RED + "Reset Class")
                .build());
        int slot = 11;
        for (ClassType classType : ClassType.values()) {
            if (classType.equals(ClassType.DEFAULT)) continue;
            classSlots.put(classType, slot);
            if (playerClass.equals(classType) || !hasClass) {
                ArrayList<String> lore = new ArrayList<>(classType.getDescription());
                lore.add("");
                inventoryBuilder.setItem(slot, new ItemBuilder(classType.getIcon())
                        .setDisplayName(classType.getDisplayName())
                        .setCustomModelData(classType.getIconId())
                        .setLore(lore)
                        .build());
            } else {
                inventoryBuilder.setItem(slot, new ItemBuilder(Material.BARRIER)
                        .setLoreWithName("You must reset your class before selecting a new one",
                                ChatColor.RED, 20)
                        .build());
            }
            slot += 2;
        }
        inventories.removeIf(inventory -> inventory.getViewers().isEmpty());
        Inventory result = inventoryBuilder.buildInventory();
        inventories.add(result);
        return result;
    }

    public static void openInventory(Player player) {
        player.openInventory(getInventory(player));
    }

    public static void handleInventory(Player player, int slot) {
        PlayerAbilityManager playerAbilityManager = PlayerSystem.getInstance().getAbilityManager(player);
        ClassType playerClass = playerAbilityManager.getClassType();
        boolean hasClass = !playerClass.equals(ClassType.DEFAULT);

        if (slot == RESET_SLOT && hasClass) {
            playerAbilityManager.setClassType(ClassType.DEFAULT);
            playerAbilityManager.resetAbilities();
            openInventory(player);
            return;
        }
        for (ClassType classType : ClassType.values()) {
            if (classSlots.get(classType) != null && classSlots.get(classType).equals(slot)) {
                // clicked on a class (that's not default)

                if (classType.equals(playerClass)) { // the player's class
                    AbilitySelectionMenu.openInventory(player);

                } else if (!hasClass) { // player has not class
                    playerAbilityManager.setClassType(classType);
                    AbilitySelectionMenu.openInventory(player);
                }
            }
        }
    }
}