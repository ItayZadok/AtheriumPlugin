package org.atheriumPlugin.items;

import org.atheriumPlugin.config.ConfigFile;
import org.atheriumPlugin.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Centralized item management system.
 * Provides clean separation for item-related functionality.
 */
public class ItemSystem {

    public static void loadAllItems() {
        FileConfiguration config = ConfigManager.getInstance().getConfig(ConfigFile.ITEM_CONFIG);
        CustomItemRegistry.loadAllFromConfig(config);
        updateAllPlayers();
    }

    private static void updateAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateInventory(player.getInventory());
        }
    }

    public static void updateInventory(PlayerInventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || !item.hasItemMeta()) continue;

            int modelData = item.getItemMeta().getCustomModelData();
            CustomItem customItem = CustomItemRegistry.getItem(modelData);
            if (customItem != null) {
                inventory.setItem(i, CustomItemRenderer.render(customItem));
            }

        }
    }
}
