package org.atheriumPlugin.gui.inventories;

import org.atheriumPlugin.abilities.AbilityType;
import org.atheriumPlugin.gui.InventoryBuilder;
import org.atheriumPlugin.utility.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class AbilityInfoMenu {

    public static final HashMap<Inventory, AbilityType> inventories = new HashMap<>();
    private static final int BACK_SLOT = 25;
    private static final int ABILITY_SLOT = 13;
    private static final int INPUT_SLOT = 11;
    private static final int INFO_SLOT = 15;

    public static Inventory getInventory(AbilityType abilityData) {
        InventoryBuilder inventoryBuilder = new InventoryBuilder(3, "Ability Info")
                .basicMenu(Material.GRAY_STAINED_GLASS_PANE);

        inventoryBuilder.setItem(INPUT_SLOT, new ItemBuilder(Material.OAK_SIGN)
                .setDisplayName(ChatColor.GOLD + "Activation")
                .setLore(ChatColor.YELLOW +
                        Arrays.toString(abilityData.getDefaultInputTypes()))
                .build());

        inventoryBuilder.setItem(ABILITY_SLOT, new ItemBuilder(abilityData.getIcon())
                .setDisplayName(ChatColor.GOLD + abilityData.getDisplayName())
                .setLore(ItemBuilder.toLore(abilityData.getAbilityDescription(),
                        ChatColor.YELLOW, 20))
                .build());

        inventoryBuilder.setItem(INFO_SLOT, new ItemBuilder(Material.DIAMOND_SWORD)
                .setDisplayName(ChatColor.GOLD + "Info")
                .setLore(ItemBuilder.toLore(abilityData.getDamageDescription(),
                        ChatColor.YELLOW, 20))
                .build());

        inventoryBuilder.setItem(BACK_SLOT, new ItemBuilder(Material.ARROW)
                .setDisplayName(ChatColor.RED + "Back")
                .build());

        for (Inventory inventory : inventories.keySet()) {
            if (inventory.getViewers().isEmpty()) {
                inventories.remove(inventory);
            }
        }

        Inventory result = inventoryBuilder.buildInventory();
        inventories.put(result, abilityData);

        return inventoryBuilder.buildInventory();
    }

    public static void openInventory(Player player, AbilityType abilityData) {
        player.openInventory(Objects.requireNonNull(getInventory(abilityData)));
    }

    public static void handleInventory(Player player, int slot) {
        if (slot == BACK_SLOT) { // back to class selection
            AbilitySelectionMenu.openInventory(player);
        }
    }
}
