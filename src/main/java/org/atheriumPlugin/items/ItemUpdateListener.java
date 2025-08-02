package org.atheriumPlugin.items;

import org.atheriumPlugin.player.PlayerSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class ItemUpdateListener implements Listener {

    @EventHandler
    public void onInventoryClick(PlayerInteractEvent event) {
        handleStats(event.getPlayer());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        handleStats(event.getPlayer());
    }

    @EventHandler
    public void onItemHeldChange(PlayerItemHeldEvent event) {
        handleStats(event.getPlayer());
    }

    @EventHandler
    public void onSwapHandItems(PlayerSwapHandItemsEvent event) {
        handleStats(event.getPlayer());
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        handleStats(event.getPlayer());
    }

    private void handleStats(Player player) {
        PlayerSystem.getInstance().getPlayerProfile(player).reloadItemStats();
    }
}
