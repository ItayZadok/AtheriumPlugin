package org.atheriumPlugin.gui.inventories;

import org.atheriumPlugin.abilities.AbilityType;
import org.atheriumPlugin.classes.ClassType;
import org.atheriumPlugin.gui.InventoryBuilder;
import org.atheriumPlugin.player.PlayerAbilityManager;
import org.atheriumPlugin.player.PlayerSystem;
import org.atheriumPlugin.utility.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AbilitySelectionMenu {

    public static final List<Inventory> inventories = new ArrayList<>();

    private static final HashMap<AbilityType, Integer> abilitySlots = new HashMap<>();
    private static final int RESET_SLOT = 36;
    private static final int BACK_SLOT = 43;
    private static final int ABILITY_POINT_SLOT = 40;

    public static Inventory getInventory(Player player) {
        PlayerAbilityManager abilityManager = PlayerSystem.getInstance().getAbilityManager(player);
        ClassType classType = abilityManager.getClassType();

        InventoryBuilder inventoryBuilder = new InventoryBuilder(5, ChatColor.DARK_GRAY +
                ChatColor.stripColor(classType.getDisplayName() + " Abilities"))
                .basicMenu(Material.GRAY_STAINED_GLASS_PANE);
        int abilityPoints = abilityManager.getAbilityPoints();
        boolean hasAbilityPoints = abilityPoints > 0;
        setButtons(inventoryBuilder, abilityPoints, hasAbilityPoints);

        int slot = 10;
        for (AbilityType ability : classType.getAbilities()) {
            abilitySlots.put(ability, slot);
            inventoryBuilder.setItem(slot,
                    createAbilityItem(ability,
                            abilityManager.isAbilityUnlocked(ability), hasAbilityPoints));
            slot += 6;
        }
        inventories.removeIf(inventory -> inventory.getViewers().isEmpty());
        Inventory result = inventoryBuilder.buildInventory();
        inventories.add(result);
        return result;
    }

    private static void setButtons(InventoryBuilder inventoryBuilder,
                                   int abilityPoints, boolean hasAbilityPoints) {
        inventoryBuilder.setItem(BACK_SLOT, new ItemBuilder(Material.ARROW).
                setDisplayName(ChatColor.RED + "Back").build());

        inventoryBuilder.setItem(RESET_SLOT, new ItemBuilder(Material.RED_TERRACOTTA).
                setDisplayName(ChatColor.RED + "Reset Abilities").build());

        inventoryBuilder.setItem(ABILITY_POINT_SLOT, new ItemBuilder(hasAbilityPoints ?
                Material.GREEN_TERRACOTTA : Material.RED_TERRACOTTA)
                .setDisplayName((hasAbilityPoints ?
                        ChatColor.GREEN : ChatColor.RED) + "Ability Points: " + abilityPoints)
                .build());
    }

    public static void openInventory(Player player) {
        player.openInventory(getInventory(player));
    }

    private static ItemStack createAbilityItem(AbilityType abilityData, boolean unlocked, boolean hasAbilityPoints) {
        if (unlocked || hasAbilityPoints) {
            return new ItemBuilder(abilityData.getIcon())
                    .setDisplayName(ChatColor.GOLD + abilityData.getDisplayName())
                    .setLore(List.of(
                            ChatColor.YELLOW + "Click to " +
                                    (unlocked ? ChatColor.RED + "disable" :
                                            ChatColor.GREEN + "enable"),
                            ChatColor.YELLOW + "this ability",
                            ChatColor.LIGHT_PURPLE + "Press Q for info"))
                    .setEnchantmentGlint(unlocked)
                    .build();
        } else {
            return new ItemBuilder(abilityData.getIcon())
                    .setLoreWithName("You don't have enough ability points to unlock this ability",
                            ChatColor.RED, 20)
                    .build();
        }
    }

    public static void handleInventory(Player player, int slot, ClickType clickType) {
        PlayerAbilityManager playerAbilityManager = PlayerSystem.getInstance().getAbilityManager(player);
        if (slot == RESET_SLOT) { // reset abilities
            playerAbilityManager.resetAbilities();
            openInventory(player);
            return;
        }
        if (slot == BACK_SLOT) { // back to class selection
            ClassSelectionMenu.openInventory(player);
            return;
        }
        // enable/disable abilities
        for (AbilityType abilityData : playerAbilityManager.getClassType().getAbilities()) { // go over class abilities
            if (abilitySlots.get(abilityData).equals(slot)) { // check if the slot is correct
                if (clickType.equals(ClickType.DROP)) {
                    AbilityInfoMenu.openInventory(player, abilityData);
                    return;
                }
                if (playerAbilityManager.isAbilityUnlocked(abilityData)) {
                    // does have the ability
                    playerAbilityManager.removeAbility(abilityData);
                } else {
                    // doesn't have the ability
                    if (playerAbilityManager.getAbilityPoints() > 0) { // can unlock the ability
                        playerAbilityManager.addAbility(abilityData);
                    }
                }
                openInventory(player);
            }
        }
    }
}