package org.atheriumPlugin.stats;

/**
 * Represents a single stat modifier with duration tracking.
 * Provides clean encapsulation for stat modifications.
 */
public class StatModifier {
    
    private final String key;
    private final CustomStat stat;
    private final double flatValue;
    private final double percentValue;
    private final long expirationTime;
    
    public StatModifier(String key, CustomStat stat, double flatValue,
                        double percentValue, long durationTicks) {
        this.key = key;
        this.stat = stat;
        this.flatValue = flatValue;
        this.percentValue = percentValue;
        
        // Calculate expiration time (-1 means permanent)
        this.expirationTime = calculateExpirationTime(durationTicks);
    }

    /**
     * Check if this modifier has expired
     */
    public boolean isExpired(long currentTime) {
        return expirationTime != -1 && currentTime >= expirationTime;
    }
    
    /**
     * Check if this modifier is permanent
     */
    public boolean isPermanent() {
        return expirationTime == -1;
    }
    
    /**
     * Get remaining duration in milliseconds
     */
    public long getRemainingDuration() {
        if (isPermanent()) {
            return -1;
        }
        return Math.max(0, expirationTime - System.currentTimeMillis());
    }

    private long calculateExpirationTime(long durationTicks) {
        if (durationTicks == -1) {
            return -1;
        } else {
            return System.currentTimeMillis() + (durationTicks * 50); // 50ms per tick
        }
    }
    
    // Getters
    public String getKey() {
        return key;
    }
    
    public CustomStat getStat() {
        return stat;
    }
    
    public double getFlatValue() {
        return flatValue;
    }
    
    public double getPercentValue() {
        return percentValue;
    }
    
    public long getExpirationTime() {
        return expirationTime;
    }
    
    @Override
    public String toString() {
        return String.format("StatModifier{key='%s', stat=%s, flat=%.2f, percent=%.2f, permanent=%s}",
                key, stat, flatValue, percentValue, isPermanent());
    }
} 