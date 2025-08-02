package org.atheriumPlugin.combat;

import org.atheriumPlugin.stats.CustomStat;
import org.atheriumPlugin.stats.StatSystem;
import org.atheriumPlugin.utility.StunEffectManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Centralized combat system that manages all combat-related functionality.
 */
public class CombatSystem {

    private final ParticleEffectManager effectManager;
    private final CombatStateTracker stateTracker;

    private static CombatSystem instance;

    public static CombatSystem getInstance() {
        if (instance == null) instance = new CombatSystem();
        return instance;
    }

    private CombatSystem() {
        this.effectManager = new ParticleEffectManager();
        this.stateTracker = new CombatStateTracker();
    }

    public void update() {
        stateTracker.update();
    }

    /**
     * Handle a player attacking an entity
     */
    public void handlePlayerAttack(Player attacker, LivingEntity target) {
        // Apply critical hit multiplier
        if (isCriticalHit(attacker)) {
            effectManager.spawnCriticalHitParticles(attacker, target);
        } else if (isCharging(attacker)) {
            effectManager.spawnSmallHitParticles(target);
        } else {
            effectManager.spawnNormalHitParticles(attacker, target);
        }
        handleMeleeAttack(attacker, target);
    }

    /**
     * Handle an entity attacking an entity (or a player)
     */
    public void handleMeleeAttack(LivingEntity attacker, LivingEntity target) {
        if (target instanceof Player player && isPlayerShielding(player)) return;
        if (StunEffectManager.isStunnedEntity(attacker)) return;
        dealTrueDamage(target, attacker, calculateMeleeDamage(attacker, target));
    }

    /**
     * Handle arrow damage
     */
    public void handleArrowDamage(LivingEntity shooter, LivingEntity target, double velocity) {
        // Apply damage
        dealTrueDamage(target, shooter, DamageCalculator.calculateArrowDamage(
                StatSystem.getStat(shooter, CustomStat.DAMAGE),
                velocity,
                StatSystem.getStat(target, CustomStat.DEFENCE),
                shooter instanceof Player
        ));
    }

    private double calculateMeleeDamage(LivingEntity attacker, LivingEntity target) {
        double baseDamage = StatSystem.getStat(attacker, CustomStat.DAMAGE);
        double defense = StatSystem.getStat(target, CustomStat.DEFENCE);

        if (attacker instanceof Player player) {
            return DamageCalculator.calculateMeleeDamage(
                    baseDamage, defense,
                    CombatSystem.isCriticalHit(player),
                    CombatSystem.isCharging(player));
        } else {
            return DamageCalculator.calculateMeleeDamage(
                    baseDamage, defense, false, true);
        }
    }

    /**
     * Deal damage to this entity (with knockback and without defense reduction)
     */
    public static void dealTrueDamage(LivingEntity target, LivingEntity attacker, double damage) {
        if (!target.isValid()) return;

        // Apply damage
        StatSystem.getInstance().getStats(target)
                .addOneTimeModifier(CustomStat.HEALTH, -damage, 0);

        // Apply knockback
        handleKnockback(target, attacker);

        target.playHurtAnimation(20);
        StatSystem.getInstance().getDamageIndicatorManager().spawnDamageIndicator(target, damage);
        CombatSystem.getInstance().updateCombatState(attacker, target);
    }

    /**
     * Deal damage to this entity (with knockback and defense reduction)
     */
    public static void dealDamage(LivingEntity target, LivingEntity attacker, double damage) {
        dealTrueDamage(target, attacker, DamageCalculator.calculateDamage(
                damage, StatSystem.getStat(target, CustomStat.DEFENCE)));
    }

    private static void handleKnockback(LivingEntity target, LivingEntity attacker) {
        // Get horizontal direction from attacker to target
        Vector direction = target.getLocation().toVector().subtract(attacker.getLocation().toVector());

        // Ignore vertical component for horizontal knockback
        direction.setY(0);

        if (direction.lengthSquared() == 0) {
            direction = attacker.getLocation().getDirection().multiply(-1); // fallback
            direction.setY(0);
        }

        direction.normalize();

        double knockbackStrength = 0.5; // Vanilla-like knockback horizontal strength
        double verticalBoost = 0.3;    // Small vertical lift

        Vector knockback = direction.multiply(knockbackStrength).setY(verticalBoost);
        target.setVelocity(knockback);
    }

    /**
     * Check if an attack is a charged hit
     */
    public static boolean isCharging(Player player) {
        return player.getAttackCooldown() == 1;
    }

    /**
     * Check if an attack is a critical hit
     */
    public static boolean isCriticalHit(Player player) {
        return isCharging(player) && player.getFallDistance() > 0.0f && !player.isOnGround();
    }

    /**
     * Check if a player is shielding
     */
    public static boolean isPlayerShielding(Player player) {
        return player.isBlocking();
    }

    public void updateCombatState(LivingEntity attacker, LivingEntity target) {
        stateTracker.updateCombatState(attacker, target);
    }

    public CombatState getCombatState(LivingEntity entity) {
        return stateTracker.getCombatState(entity);
    }

    public boolean isInCombat(LivingEntity entity) {
        return stateTracker.isInCombat(entity);
    }

    /**
     * Clean up the combat system
     */
    public void cleanup() {
        stateTracker.cleanup();
    }
}