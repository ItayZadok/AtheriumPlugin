package org.atheriumPlugin.combat;

import org.bukkit.entity.LivingEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CombatStateTracker {

    private final Map<LivingEntity, CombatState> combatStates = new ConcurrentHashMap<>();

    /**
     * Update combat state for entities
     */
    public void updateCombatState(LivingEntity attacker, LivingEntity target) {
        long currentTime = System.currentTimeMillis();

        // Update attacker combat state
        CombatState attackerState = combatStates
                .computeIfAbsent(attacker, k -> new CombatState());
        attackerState.setLastAttackTime(currentTime);
        attackerState.setLastTarget(target);

        // Update target combat state
        CombatState targetState = combatStates
                .computeIfAbsent(target, k -> new CombatState());
        targetState.setLastAttackedTime(currentTime);
        targetState.setLastAttacker(attacker);
    }

    /**
     * Clean up expired or invalid combat states
     */
    public void update() {
        combatStates.entrySet().removeIf(entry -> {
            CombatState state = entry.getValue();
            return state.isExpired();
        });
    }

    /**
     * Clear all combat states
     */
    public void cleanup() {
        combatStates.clear();
    }

    /**
     * Get combat state for an entity
     */
    public CombatState getCombatState(LivingEntity entity) {
        return combatStates.get(entity);
    }

    /**
     * Check if an entity is in combat
     */
    public boolean isInCombat(LivingEntity entity) {
        CombatState state = combatStates.get(entity);
        return state != null && !state.isExpired();
    }
}
