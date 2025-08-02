package org.atheriumPlugin.player;

import org.atheriumPlugin.stats.StatSystem;
import org.atheriumPlugin.utility.Logger;
import org.bukkit.entity.Player;

/**
 * Represents a player's profile with all associated systems.
 * Consolidates player data management.
 */
public class PlayerProfile {
    
    private final Player player;
    private final StatSystem statSystem;
    private final PlayerItemManager itemManager;
    private final PlayerItemStatsManager itemStatManager;
    private final PlayerAbilityManager abilityManager;
    
    public PlayerProfile(Player player) {
        this.player = player;
        this.statSystem = StatSystem.getInstance();
        this.itemManager = new PlayerItemManager(player);
        this.itemStatManager = new PlayerItemStatsManager(player);
        this.abilityManager = new PlayerAbilityManager(player.getUniqueId());
    }
    
    /**
     * Update the player's systems
     */
    public void update() {
        reloadItemStats();
        statSystem.getStats(player).update();
    }
    
    /**
     * Reload item stats if inventory has changed
     */
    public void reloadItemStats() {
        if (itemManager.isNewHash()) {
            itemManager.reloadItems();
            itemStatManager.updateStatsFromItems(itemManager);
        }
    }
    
    /**
     * Save player data
     */
    public void saveData() {
        abilityManager.saveToConfig();
    }
    
    // Getters
    public Player getPlayer() {
        return player;
    }
    
    public PlayerItemManager getItemManager() {
        return itemManager;
    }

    public PlayerAbilityManager getAbilityManager() {
        return abilityManager;
    }
}