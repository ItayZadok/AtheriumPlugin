package org.atheriumPlugin.abilities;

import org.atheriumPlugin.player.PlayerAbility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Centralized ability management system.
 * Replaces the old ability management with better organization and efficiency.
 */
public class AbilitySystem {

    private final Map<Player, Map<PlayerAbility, Event>> queuedAbilities
            = new ConcurrentHashMap<>();

    private static AbilitySystem instance;

    public static AbilitySystem getInstance() {
        if (instance == null) instance = new AbilitySystem();
        return instance;
    }

    private AbilitySystem() {
    }

    /**
     * Register all abilities
     */
    public void registerAllAbilities() {
        // Register abilities from AbilityData enum
        for (AbilityType abilityData : AbilityType.values()) {
            AbilityBase ability = abilityData.getAbilityBase();
            ability.onRegister();
        }
    }

    /**
     * Update all abilities
     */
    public void updateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            executeQueuedAbilities(player);
        }
        for (AbilityType abilityData : AbilityType.values()) {
            abilityData.getAbilityBase().periodic();
        }
    }

    /**
     * Queue an ability for execution
     */
    public void queueAbility(Player player, PlayerAbility playerAbility, Event event) {
        queuedAbilities.computeIfAbsent(player, k -> new ConcurrentHashMap<>())
                .put(playerAbility, event);

    }

    /**
     * Execute queued abilities for a player
     */
    public void executeQueuedAbilities(Player player) {
        Map<PlayerAbility, Event> playerAbilities = queuedAbilities.get(player);

        if (playerAbilities != null) {
            playerAbilities.forEach(PlayerAbility::execute);
            playerAbilities.clear();
        }
    }

    /**
     * Clean up the ability system
     */
    public void cleanup() {
        for (AbilityType abilityData : AbilityType.values()) {
            abilityData.getAbilityBase().onDisable();
        }
    }
}