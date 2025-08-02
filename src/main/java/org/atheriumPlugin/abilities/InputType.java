package org.atheriumPlugin.abilities;

import org.atheriumPlugin.combat.CombatSystem;
import org.atheriumPlugin.items.ItemType;
import org.atheriumPlugin.player.PlayerSystem;
import org.atheriumPlugin.utility.AbilityUtility;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.EquipmentSlot;

public enum InputType {

    RIGHT_CLICK {
        @Override
        public boolean isTriggered(Player player, Event event) {
            return event instanceof PlayerInteractEvent interactEvent &&
                    (interactEvent.getAction() == Action.RIGHT_CLICK_AIR || interactEvent.getAction() == Action.RIGHT_CLICK_BLOCK);
        }
    },
    LEFT_CLICK {
        @Override
        public boolean isTriggered(Player player, Event event) {
            return (event instanceof PlayerInteractEvent interactEvent && (interactEvent.getAction() == Action.LEFT_CLICK_AIR
                    || interactEvent.getAction() == Action.LEFT_CLICK_BLOCK))
                    || event instanceof EntityDamageByEntityEvent damageEvent && damageEvent.getDamager().equals(player);
        }
    },
    SHIFT {
        @Override
        public boolean isTriggered(Player player, Event event) {
            return player.isSneaking();
        }
    },
    Q {
        @Override
        public boolean isTriggered(Player player, Event event) {
            return event instanceof PlayerDropItemEvent;
        }
    },
    F {
        @Override
        public boolean isTriggered(Player player, Event event) {
            return event instanceof PlayerSwapHandItemsEvent;
        }
    },
    DOUBLE_SPACE {
        @Override
        public boolean isTriggered(Player player, Event event) {
            if (event instanceof PlayerToggleFlightEvent flightEvent) {
                if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                    flightEvent.setCancelled(true);
                    player.setFlying(false);
                }
                return true;
            }
            return false;
        }
    },
    KILL {
        @Override
        public boolean isTriggered(Player player, Event event) {
            return event instanceof EntityDeathEvent deathEvent &&
                    CombatSystem.getInstance().getCombatState(
                            deathEvent.getEntity()).getLastAttacker() == player;
        }
    },
    SHOOT {
        @Override
        public boolean isTriggered(Player player, Event event) {
            return event instanceof EntityShootBowEvent shootEvent && shootEvent.getEntity().equals(player);
        }
    },
    HIT {
        @Override
        public boolean isTriggered(Player player, Event event) {
            return event instanceof EntityDamageByEntityEvent damageEvent && damageEvent.getDamager().equals(player);
        }
    },
    CRITICAL {
        @Override
        public boolean isTriggered(Player player, Event event) {
            return event instanceof EntityDamageByEntityEvent damageEvent &&
                    damageEvent.getDamager().equals(player) && AbilityUtility.isCritical(player);
        }
    },
    CHARGE_SHIELD {
        @Override
        public boolean isTriggered(Player player, Event event) {
            return AbilityUtility.isChargingShield(player);
        }
    },
    AIR {
        @Override
        public boolean isTriggered(Player player, Event event) {
            return !player.isOnGround();
        }
    },
    DAMAGED {
        @Override
        public boolean isTriggered(Player player, Event event) {
            return event instanceof EntityDamageByEntityEvent damageEvent && damageEvent.getEntity().equals(player);
        }
    };

    public abstract boolean isTriggered(Player player, Event event);

    public static boolean isHolding(Player player, EquipmentSlot slot, ItemType itemType) {
        try {
            return PlayerSystem.getInstance().getItemManager(player).getItem(slot)
                    .itemType().equals(itemType);
        } catch (NullPointerException e) {
            return false;
        }
    }
}