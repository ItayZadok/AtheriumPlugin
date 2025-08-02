package org.atheriumPlugin.combat;

import org.atheriumPlugin.items.ItemType;
import org.atheriumPlugin.player.PlayerItemManager;
import org.atheriumPlugin.player.PlayerSystem;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * combat listener
 */
public class CombatListener implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        // Cancel vanilla damage
        event.setCancelled(true);

        if (event.getDamager() instanceof Player player) {
            handlePlayerAttack(player, target);

        } else if (event.getDamager() instanceof LivingEntity attacker) {
            CombatSystem.getInstance().handleMeleeAttack(attacker, target);

        } else if (event.getDamager() instanceof Arrow arrow) {
            handleArrowDamage(arrow, target);
        }
    }

    private void handlePlayerAttack(Player player, LivingEntity target) {
        PlayerItemManager itemManager = PlayerSystem.getInstance().getItemManager(player);

        // Check if player is holding a bow
        if (itemManager.getItem(EquipmentSlot.HAND) != null &&
                itemManager.getItem(EquipmentSlot.HAND).itemType().equals(ItemType.BOW)) {
            return; // Don't handle melee damage for bow users
        }

        CombatSystem.getInstance().handlePlayerAttack(player, target);
    }

    private void handleArrowDamage(Arrow arrow, LivingEntity target) {
        if (!(arrow.getShooter() instanceof LivingEntity entity)) return;
        if (target instanceof Player player && isPlayerShielding(player)) return;

        double velocity = arrow.getVelocity().length();
        arrow.remove();
        CombatSystem.getInstance().handleArrowDamage(entity, target, velocity);
    }

    private boolean isPlayerShielding(Player player) {
        return player.isBlocking();
    }
} 