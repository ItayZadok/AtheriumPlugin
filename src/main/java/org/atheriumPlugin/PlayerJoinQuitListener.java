package org.atheriumPlugin;

import org.atheriumPlugin.items.ItemSystem;
import org.atheriumPlugin.player.PlayerSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles player join and quit events using the new architecture.
 */
public class PlayerJoinQuitListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Initialize player profile and reload item stats
        PlayerSystem.getInstance().getPlayerProfile(event.getPlayer()).reloadItemStats();
        ItemSystem.updateInventory(event.getPlayer().getInventory());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Save player data and remove profile
        PlayerSystem.getInstance().removePlayerProfile(event.getPlayer());
    }
} 