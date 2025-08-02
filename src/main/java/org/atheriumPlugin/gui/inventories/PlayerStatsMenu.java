package org.atheriumPlugin.gui.inventories;

import org.atheriumPlugin.gui.InventoryBuilder;
import org.atheriumPlugin.items.CustomItemRenderer;
import org.atheriumPlugin.stats.CustomStat;
import org.atheriumPlugin.stats.StatSystem;
import org.atheriumPlugin.utility.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerStatsMenu {

    public static final List<Inventory> inventories = new ArrayList<>();

    public static Inventory getInventory(LivingEntity target) {
        InventoryBuilder inventoryBuilder = new InventoryBuilder(3, "Class Selection")
                .basicMenu(Material.GRAY_STAINED_GLASS_PANE);
        List<String> lore = new ArrayList<>();

        for (CustomStat stat : CustomStat.values()) {
            if (stat == CustomStat.HEALTH) continue; // displaying health is weird

            double total = StatSystem.getStat(target, stat);
            double flat = StatSystem.getInstance().getStats(target).getFlatStat(stat);
            double percent = StatSystem.getInstance().getStats(target).getPercentStat(stat);

            String percentStr = String.format("%s%.1f%%", (percent >= 0 ? "+" : ""), percent);

            String line = String.format("%s %s%.1f§8 (%.1f %s)",
                    stat.getDisplayName(),
                    stat.getColor(),
                    total,
                    flat,
                    percentStr
            );

            lore.add(line);
        }

        inventoryBuilder.setItem(13, new ItemBuilder(Material.BOOK)
                .setDisplayName("&7Stats").setLore(lore).build());

        inventories.removeIf(inventory -> inventory.getViewers().isEmpty());
        Inventory result = inventoryBuilder.buildInventory();
        inventories.add(result);
        return result;
    }

    public static void openInventory(Player player, LivingEntity target) {
        player.openInventory(Objects.requireNonNull(getInventory(target)));
    }
}
