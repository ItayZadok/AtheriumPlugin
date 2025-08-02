package org.atheriumPlugin.abilities;

import org.atheriumPlugin.AtheriumPlugin;
import org.atheriumPlugin.classes.ClassType;
import org.atheriumPlugin.combat.CombatSystem;
import org.atheriumPlugin.player.PlayerAbility;
import org.atheriumPlugin.player.PlayerSystem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class AbilityExecuteListener implements Listener {

    private final Set<UUID> tempDropList = new LinkedHashSet<>();

    @EventHandler
    public void onInventoryOpen(InventoryClickEvent event) {
        if (event.getSlot() == -999
                || ((event.getClick() == ClickType.DROP || event.getClick() == ClickType.CONTROL_DROP)
                || event.getClick() == ClickType.CREATIVE)) {

            UUID uuid = event.getWhoClicked().getUniqueId();
            tempDropList.add(uuid);

            Bukkit.getScheduler().runTaskLater(AtheriumPlugin.getInstance(),
                    () -> tempDropList.remove(uuid), 5);
        }
    }

    @EventHandler
    public void onPlayerRightOrLeftClick(PlayerInteractEvent event) {
        executeAbility(event.getPlayer(), event);
    }

    @EventHandler
    public void onPlayerQ(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (tempDropList.contains(event.getPlayer().getUniqueId())) return;

        ItemStack item = event.getItemDrop().getItemStack();
        if (ClassType.isClassItem(player, item)) {
            event.setCancelled(true);
            executeAbility(player, event);
        }
    }

    @EventHandler
    public void onPlayerShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player player)
            executeAbility(player, event);
    }

    @EventHandler
    public void onPlayerShift(PlayerToggleSneakEvent event) {
        executeAbility(event.getPlayer(), event);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player)
            executeAbility(player, event);
        if (event.getDamager() instanceof Player player)
            executeAbility(player, event);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        LivingEntity entity = CombatSystem.getInstance().
                getCombatState(event.getEntity()).getLastAttacker();
        if (entity.isValid() && entity instanceof Player player) {
            Bukkit.getScheduler().runTaskLater(AtheriumPlugin.getInstance(),
                    () -> executeAbility(player, event), 20);
            // trigger after the death animation
        }
    }

    @EventHandler
    public void onPlayerPressF(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHandItem = event.getMainHandItem();
        ItemStack offHandItem = event.getOffHandItem();
        if (ClassType.isClassItem(player, mainHandItem) ||
                ClassType.isClassItem(player, offHandItem)) {
            event.setCancelled(true);
            executeAbility(event.getPlayer(), event);
        }
    }

    @EventHandler
    public void onPlayerDoubleSpace(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
        executeAbility(event.getPlayer(), event);
    }

    public static void executeAbility(Player player, Event event) {
        Set<PlayerAbility> abilities = PlayerSystem.getInstance()
                .getAbilityManager(player)
                .getUnlockedAbilities();

        for (PlayerAbility ability : abilities) {
            boolean allTriggered = true;

            for (InputType input : ability.getInputTypes()) {
                if (!input.isTriggered(player, event)) {
                    allTriggered = false;
                    break;
                }
            }

            if (allTriggered) {
                AbilitySystem.getInstance().queueAbility(player, ability, event);
                return; // Only execute the first matching ability
            }
        }
    }
}