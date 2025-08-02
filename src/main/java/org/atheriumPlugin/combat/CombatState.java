package org.atheriumPlugin.combat;

import org.bukkit.entity.LivingEntity;

/**
 * Tracks combat state for an entity.
 * Stores information about recent combat interactions.
 */
public class CombatState {

    private long lastAttackTime = 0;
    private long lastAttackedTime = 0;
    private LivingEntity lastTarget = null;
    private LivingEntity lastAttacker = null;

    private static final long COMBAT_TIMEOUT = 5000; // 5 seconds

    /**
     * Check if this combat state has expired
     */
    public boolean isExpired() {
        return (System.currentTimeMillis() - lastAttackTime > COMBAT_TIMEOUT) &&
                (System.currentTimeMillis() - lastAttackedTime > COMBAT_TIMEOUT);
    }

    /**
     * Check if the entity is currently in combat
     */
    public boolean isInCombat() {
        return !isExpired();
    }

    /**
     * Get time since last attack
     */
    public long getTimeSinceLastAttack() {
        return System.currentTimeMillis() - lastAttackTime;
    }

    /**
     * Get time since last attacked
     */
    public long getTimeSinceLastAttacked() {
        return System.currentTimeMillis() - lastAttackedTime;
    }

    // Getters and setters
    public long getLastAttackTime() {
        return lastAttackTime;
    }

    public void setLastAttackTime(long lastAttackTime) {
        this.lastAttackTime = lastAttackTime;
    }

    public long getLastAttackedTime() {
        return lastAttackedTime;
    }

    public void setLastAttackedTime(long lastAttackedTime) {
        this.lastAttackedTime = lastAttackedTime;
    }

    public LivingEntity getLastTarget() {
        return lastTarget;
    }

    public void setLastTarget(LivingEntity lastTarget) {
        this.lastTarget = lastTarget;
    }

    public LivingEntity getLastAttacker() {
        return lastAttacker;
    }

    public void setLastAttacker(LivingEntity lastAttacker) {
        this.lastAttacker = lastAttacker;
    }
} 