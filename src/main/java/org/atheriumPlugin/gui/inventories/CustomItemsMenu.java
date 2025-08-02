package org.atheriumPlugin.gui.inventories;

import org.atheriumPlugin.gui.InventoryBuilder;
import org.atheriumPlugin.items.CustomItem;
import org.atheriumPlugin.items.CustomItemRegistry;
import org.atheriumPlugin.items.CustomItemRenderer;
import org.atheriumPlugin.utility.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class CustomItemsMenu {

    public static final Set<Inventory> inventories = new LinkedHashSet<>();
    private static final Map<UUID, Integer> playerPages = new HashMap<>();
    private static final int ITEMS_PER_PAGE = 45; // 5 rows (0-44)

    public static void openInventory(Player player, int page) {
        int totalItems = CustomItemRegistry.getItemAmount();
        int totalPages = (int) Math.ceil(totalItems / (double) ITEMS_PER_PAGE);
        page = Math.max(0, Math.min(page, totalPages - 1)); // clamp page

        InventoryBuilder builder = new
                InventoryBuilder(6, "Custom Items - Page " + (page + 1))
                .basicMenu(Material.GRAY_STAINED_GLASS_PANE);

        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, totalItems);

        for (int i = start; i < end; i++) {
            CustomItem customItem = CustomItemRegistry.getItem(i + 1); // registry starts at 1
            if (customItem != null)
                builder.setItem(i - start, CustomItemRenderer.render(customItem));
        }

        // Navigation buttons
        if (page > 0) {
            builder.setItem(45, ItemBuilder.quick(Material.ARROW, "§aPrevious Page"));
        }
        if (page < totalPages - 1) {
            builder.setItem(52, ItemBuilder.quick(Material.ARROW, "§aNext Page"));
        }

        playerPages.put(player.getUniqueId(), page);
        Inventory inventory = builder.buildInventory();
        inventories.add(inventory);

        player.openInventory(inventory);
    }

    public static void openInventory(Player player) {
        openInventory(player, 0);
    }

    public static void handleInventory(Player player, int slot) {
        int page = playerPages.getOrDefault(player.getUniqueId(), 0);

        // Handle navigation
        if (slot == 45) {
            openInventory(player, page - 1);
            return;
        } else if (slot == 52) {
            openInventory(player, page + 1);
            return;
        }
        if (slot > 45 && slot < 53) return;

        int itemIndex = page * ITEMS_PER_PAGE + slot;
        CustomItem customItem = CustomItemRegistry.getItem(itemIndex + 1);
        if (customItem == null) return;

        player.getInventory().addItem(CustomItemRenderer.render(customItem));
    }
}
