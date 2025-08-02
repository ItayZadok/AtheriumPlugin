package org.atheriumPlugin.stats;

import org.atheriumPlugin.AtheriumPlugin;
import org.atheriumPlugin.utility.Logger;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Efficient stat management for a single entity.
 */
public class EntityStats {

    private final LivingEntity entity;
    private final Map<CustomStat, Double> flatModifiers = new ConcurrentHashMap<>();
    private final Map<CustomStat, Double> percentModifiers = new ConcurrentHashMap<>();
    private final Map<String, StatModifier> activeModifiers = new ConcurrentHashMap<>();
    private final Map<CustomStat, Double> cachedStats = new ConcurrentHashMap<>();
    private boolean cacheIsValid = false;

    public EntityStats(LivingEntity entity) {
        this.entity = entity;
        initializeDefaultStats();
    }

    private void initializeDefaultStats() {
        setBaseStat(CustomStat.MAX_HEALTH, 20);
        setBaseStat(CustomStat.DAMAGE, 1);
        setBaseStat(CustomStat.DEFENCE, 0);
        setBaseStat(CustomStat.SPEED, 20);
        setBaseStat(CustomStat.ATTACK_SPEED, 10);
        addOneTimeModifier(CustomStat.HEALTH, getStat(CustomStat.MAX_HEALTH), 0);
    }

    // Get final calculated stat value (cached)
    public double getStat(CustomStat stat) {
        if (stat.equals(CustomStat.HEALTH) && cachedStats.get(stat) != entity.getHealth()) {
            double newStats = calculateStat(stat);
            cachedStats.put(stat, newStats);
            return newStats;
        }

        if (!cacheIsValid) {
            recalculateStats();
            cacheIsValid = true;
        }

        return cachedStats.getOrDefault(stat, 0.0);
    }

    // Get flat (base) value of stat
    public double getFlatStat(CustomStat stat) {
        return flatModifiers.getOrDefault(stat, 0.0);
    }

    // Get percent modifier of stat
    public double getPercentStat(CustomStat stat) {
        return percentModifiers.getOrDefault(stat, 0.0);
    }

    private void recalculateStats() {
        for (CustomStat stat : CustomStat.values()) {
            cachedStats.put(stat, calculateStat(stat));
        }
    }

    private double calculateStat(CustomStat stat) {
        double flat = flatModifiers.getOrDefault(stat, 0.0);
        double percent = percentModifiers.getOrDefault(stat, 0.0);

        if (stat == CustomStat.HEALTH) {
            return entity.getHealth() + flat;
        }

        return flat * (1.0 + percent / 100.0);
    }

    private String getFullKey(String base, CustomStat stat) {
        return base + ":" + stat.name();
    }

    public void addPermanentModifier(String key, CustomStat stat, double flatValue, double percentValue) {
        addModifier(key, stat, flatValue, percentValue, -1);
    }

    public void addOneTimeModifier(CustomStat stat, double flatValue, double percentValue) {
        addModifier(UUID.randomUUID().toString(), stat, flatValue, percentValue, 1);
    }

    public void healInterval(double flatValue, long durationInTicks, long intervalInTicks) {
        long[] elapsed = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                addOneTimeModifier(CustomStat.HEALTH, flatValue, 0);
                elapsed[0] += intervalInTicks;
                if (elapsed[0] >= durationInTicks) cancel();
            }
        }.runTaskTimer(AtheriumPlugin.getInstance(), 0, intervalInTicks);
    }

    public void addModifier(String key, CustomStat stat, double flatValue, double percentValue, long durationTicks) {
        removeModifier(key, stat);
        String fullKey = getFullKey(key, stat);

        StatModifier modifier = new StatModifier(fullKey, stat, flatValue, percentValue, durationTicks);
        activeModifiers.put(fullKey, modifier);

        if (flatValue != 0) flatModifiers.merge(stat, flatValue, Double::sum);
        if (percentValue != 0) percentModifiers.merge(stat, percentValue, Double::sum);

        invalidateCache();
    }

    public void removeModifier(String key, CustomStat stat) {
        if (stat.equals(CustomStat.HEALTH)) Logger.log(key);

        StatModifier modifier = activeModifiers.remove(getFullKey(key, stat));
        if (modifier != null) removeModifierInternally(modifier);
    }

    private void removeModifierInternally(StatModifier modifier) {
        subtractModifier(flatModifiers, modifier.getStat(), modifier.getFlatValue());
        subtractModifier(percentModifiers, modifier.getStat(), modifier.getPercentValue());
        invalidateCache();
    }

    private void subtractModifier(Map<CustomStat, Double> map, CustomStat stat, double value) {
        if (value == 0) return;
        map.merge(stat, -value, Double::sum);
        if (Math.abs(map.get(stat)) < 1e-6) {
            map.remove(stat);
        }
    }

    public void update() {
        cleanupExpiredModifiers();
        applyStatsToEntity();
    }

    private void cleanupExpiredModifiers() {
        long currentTime = System.currentTimeMillis();

        activeModifiers.entrySet().removeIf(entry -> {
            StatModifier modifier = entry.getValue();
            if (modifier.isExpired(currentTime)) {
                removeModifierInternally(modifier);
                return true;
            }
            return false;
        });
    }

    private void applyStatsToEntity() {
        if (entity.isDead()) return;

        for (CustomStat stat : CustomStat.values()) {
            double value = getStat(stat);
            stat.set(entity, value);
        }
    }

    public void setBaseStat(CustomStat stat, double value) {
        addPermanentModifier("base", stat, value, 0);
    }

    private void invalidateCache() {
        cacheIsValid = false;
    }
}
