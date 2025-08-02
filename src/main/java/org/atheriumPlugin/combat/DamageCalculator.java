package org.atheriumPlugin.combat;

import org.atheriumPlugin.utility.Logger;

/**
 * Handles damage calculations for combat.
 */
public class DamageCalculator {

    public static double calculateMeleeDamage(double baseDamage,
                                              double defense,
                                              boolean isCritical,
                                              boolean isCharged) {
        double dmg = baseDamage * (isCritical ? 1.5 : 1.0) * (isCharged ? 1.0 : 0.25);
        return dmg * calculateDamageReduction(defense);
    }

    public static double calculateDamage(double damage,
                                         double defense) {
        return damage * calculateDamageReduction(defense);
    }

    /**
     * Calculate damage reduction based on defense
     */
    public static double calculateDamageReduction(double defense) {
        if (defense >= 0) return 1.0 - (defense / (defense + 30.0));
        return 1.0 + (Math.abs(defense) / (Math.abs(defense) + 15.0));
    }

    public static double calculateArrowDamage(double baseDamage,
                                              double velocity,
                                              double defense,
                                              boolean isShooterPlayer) {
        if (isShooterPlayer) {
            int level = (int) Math.ceil(Math.min(velocity, 2.147483647E9));
            if (level > 3) level = 3;

            baseDamage = baseDamage / 3 * level;
        }
        return baseDamage * calculateDamageReduction(defense);
    }
} 