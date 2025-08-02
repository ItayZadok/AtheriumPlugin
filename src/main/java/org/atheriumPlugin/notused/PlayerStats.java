//package org.atheriumPlugin.notused;
//
//import org.atheriumPlugin.items.CustomItem;
//import org.atheriumPlugin.items.InventorySlot;
//import org.atheriumPlugin.items.ItemConfigReader;
//import org.atheriumPlugin.stats.CustomStat;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.PlayerInventory;
//
//import java.util.HashMap;
//import java.util.Objects;
//
//import static org.atheriumPlugin.items.InventorySlot.*;
//
//public class PlayerStats {
//
//    private static final HashMap<Player, PlayerStats> playerStats = new HashMap<>();
//
//    private final HashMap<InventorySlot, CustomItem> itemsMap = new HashMap<>() {{
//        put(MAIN_HAND, null);
//        put(OFF_HAND, null);
//        put(BOOTS, null);
//        put(LEGGINGS, null);
//        put(CHESTPLATE, null);
//        put(HELMET, null);
//    }};
//
//    private final HashMap<CustomStat, Double> statsMap = new HashMap<>() {{
//        for (CustomStat stat : CustomStat.values()) {
//            put(stat, stat.getBase());
//        }
//    }};
//
//    public PlayerStats(Player player) {
//        playerStats.put(player, this);
//    }
//
//    public static PlayerStats getPlayerStats(Player player) {
//        if (!playerStats.containsKey(player)) {
//            playerStats.put(player, new PlayerStats(player));
//        }
//        return playerStats.get(player);
//    }
//
//    public HashMap<CustomStat, Double> getStatsMap(PlayerStats playerStats) {
//        return playerStats.statsMap;
//    }
//
//    public void setItem(InventorySlot slot, CustomItem item) {
//        itemsMap.put(slot, item);
//    }
//
//    public static double getStat(CustomStat customStat, PlayerStats playerStats) {
//        return playerStats.statsMap.getOrDefault(customStat, customStat.getBase());
//    }
//
//    public CustomItem getItem(InventorySlot slot) {
//        return itemsMap.get(slot);
//    }
//
//    private static void reloadItems(PlayerStats playerStats, Player player) {
//        PlayerInventory inventory = player.getInventory();
//        playerStats.itemsMap.forEach((slot, curItem) -> {
//            ItemStack itemStack = slot.getItem(inventory);
//            CustomItem customItem = (itemStack != null && itemStack.hasItemMeta())
//                    ? ItemConfigReader.getItem(getItemId(itemStack)) : null;
//            playerStats.setItem(slot, customItem != null && customItem.itemType().getSlot().equals(slot) ?
//                    customItem : null);
//        });
//    }
//
//    private static void applyStatsMap(Player player) {
//        PlayerStats playerStats = PlayerStats.getPlayerStats(player);
//        if (playerStats == null) return;
//        for (CustomStat stat : playerStats.statsMap.keySet()) {
//            double value = getStat(stat, playerStats);
//            stat.set(player, value);
//        }
//    }
//
//    private static void updateStatsMap(PlayerStats playerStats) {
//        playerStats.statsMap.replaceAll((s, v) -> s.getBase()); // reset stats to base
//        for (CustomItem item : playerStats.itemsMap.values()) {
//            if (item == null) continue;
//            if (item.itemType() != null && item.itemType().getSlot() != null) {
//                for (CustomStat stat : item.stats().keySet()) {
//                    double cur = getStat(stat, playerStats);
//                    double value = item.stats().get(stat);
//                    playerStats.statsMap.put(stat, cur + value);
//                }
//            }
//        }
//    }
//
//    private static int getItemId(ItemStack itemStack) {
//        return Objects.requireNonNull(itemStack.getItemMeta()).hasCustomModelData() ?
//                itemStack.getItemMeta().getCustomModelData() : 0;
//    }
//
//    public static void reloadPlayerStats(Player player) {
//        PlayerStats playerStats = PlayerStats.getPlayerStats(player);
//        if (playerStats == null) return;
//        reloadItems(playerStats, player);
//        updateStatsMap(playerStats);
//        applyStatsMap(player);
//    }
//}
