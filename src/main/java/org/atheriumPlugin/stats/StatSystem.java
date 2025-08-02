package org.atheriumPlugin.stats;

import org.atheriumPlugin.utility.Logger;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Centralized stat management system.
 * Provides efficient stat tracking and modification for all entities.
 */
public class StatSystem {

    private final Map<LivingEntity, EntityStats> entityStatsMap = new ConcurrentHashMap<>();
    private final DamageIndicatorManager damageIndicatorManager;

    private static StatSystem instance;

    public static StatSystem getInstance() {
        if (instance == null) instance = new StatSystem();
        return instance;
    }

    private StatSystem() {
        this.damageIndicatorManager = new DamageIndicatorManager();
    }

    public static double getStat(LivingEntity entity, CustomStat stat) {
        return getInstance().getStats(entity).getStat(stat);
    }

    public Set<LivingEntity> getAllEntities() {
        return entityStatsMap.keySet();
    }

    /**
     * Get or create stats for an entity
     */
    public EntityStats getStats(LivingEntity entity) {
        return entityStatsMap.computeIfAbsent(entity, EntityStats::new);
    }

    /**
     * Update all entity stats
     */
    public void updateAll() {
        entityStatsMap.entrySet().removeIf(entry -> {
            LivingEntity entity = entry.getKey();
            EntityStats stats = entry.getValue();

            if ((entity.isDead() || !entity.isValid()) && !(entity instanceof Player)) {
                return true; // Remove dead/invalid entities
            }

            stats.update();
            HealthBarManager.updateBar(entity);
            return false;
        });
    }

    /**
     * Clean up dead entities
     */
    public void cleanupDeadEntities() {
        entityStatsMap.entrySet().removeIf(entry -> {
            LivingEntity entity = entry.getKey();
            return entity.isDead() || !entity.isValid();
        });
    }

    /**
     * Clean up all stats and indicators
     */
    public void cleanup() {
        entityStatsMap.clear();
        damageIndicatorManager.cleanup();
        HealthBarManager.cleanup();
    }

    /**
     * Get the damage indicator manager
     */
    public DamageIndicatorManager getDamageIndicatorManager() {
        return damageIndicatorManager;
    }
}
