package org.atheriumPlugin.player;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Centralized player management system.
 */
public class PlayerSystem {

    private final Map<Player, PlayerProfile> playerProfiles = new ConcurrentHashMap<>();

    private static PlayerSystem instance;

    public static PlayerSystem getInstance() {
        if (instance == null) instance = new PlayerSystem();
        return instance;
    }

    private PlayerSystem() {
    }

    /**
     * Get or create a player profile
     */
    public PlayerProfile getPlayerProfile(Player player) {
        return playerProfiles.computeIfAbsent(player, PlayerProfile::new);
    }

    /**
     * Update a player's systems
     */
    public void updatePlayer(Player player) {
        PlayerProfile profile = getPlayerProfile(player);
        profile.update();
    }

    /**
     * Save all player data
     */
    public void saveAllPlayerData() {
        playerProfiles.values().forEach(PlayerProfile::saveData);
    }

    /**
     * Remove a player profile
     */
    public void removePlayerProfile(Player player) {
        PlayerProfile profile = playerProfiles.remove(player);
        if (profile != null) {
            profile.saveData();
        }
    }

    public PlayerItemManager getItemManager(Player player) {
        return getPlayerProfile(player).getItemManager();
    }

    public PlayerAbilityManager getAbilityManager(Player player) {
        return getPlayerProfile(player).getAbilityManager();
    }

    /**
     * Clean up all player profiles
     */
    public void cleanup() {
        saveAllPlayerData();
        playerProfiles.clear();
    }
} 