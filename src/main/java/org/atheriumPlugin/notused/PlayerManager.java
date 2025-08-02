//package org.atheriumPlugin.player;
//
//import org.atheriumPlugin.items.CustomItemManager;
//import org.atheriumPlugin.stats.StatEntity;
//import org.bukkit.entity.Player;
//
//import java.util.HashMap;
//
//public class PlayerManager {
//
//    private static final HashMap<Player, PlayerManager> combatProfiles = new HashMap<>();
//    private final PlayerItemManager itemManager;
//    private final PlayerItemStatsManager statManager;
//    private final PlayerAbilityManager abilityManager;
//    private final Player player;
//
//    public PlayerManager(Player player) {
//        this.player = player;
//        this.itemManager = new PlayerItemManager(player);
//        this.statManager = new PlayerItemStatsManager(player);
//        this.abilityManager = new PlayerAbilityManager(player.getUniqueId());
//        CustomItemManager.updateInventory(player.getInventory());
//    }
//
//    public static PlayerManager getProfile(Player player) {
//        return combatProfiles.computeIfAbsent(player, PlayerManager::new);
//    }
//
//    public static void saveAllPlayerData() {
//        for (PlayerManager playerManager : combatProfiles.values()) {
//            playerManager.saveData();
//        }
//    }
//
//    public void saveData() {
//        abilityManager.saveToConfig();
//    }
//
//    public void reloadItemStats() {
//        if (itemManager.isNewHash()) {
//            itemManager.reloadItems();
//            statManager.updateStatsFromItems(itemManager);
//        }
//    }
//
//    public void updateStats() {
//        reloadItemStats();
//        StatEntity.getPlayerStats(player).periodic();
//    }
//
//    public PlayerItemManager getItemManager() {
//        combatProfiles.computeIfAbsent(player, PlayerManager::new);
//        return itemManager;
//    }
//
//    public PlayerItemStatsManager getStatManager() {
//        combatProfiles.computeIfAbsent(player, PlayerManager::new);
//        return statManager;
//    }
//
//    public PlayerAbilityManager getAbilityManager() {
//        return abilityManager;
//    }
//}
//
