package org.atheriumPlugin.abilities.knight;

import org.atheriumPlugin.AtheriumPlugin;
import org.atheriumPlugin.abilities.AbilityBase;
import org.atheriumPlugin.combat.CombatSystem;
import org.atheriumPlugin.stats.CustomStat;
import org.atheriumPlugin.stats.StatSystem;
import org.atheriumPlugin.utility.AbilityUtility;
import org.atheriumPlugin.utility.StunEffectManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Objects;

public class ShieldStunAbility extends AbilityBase {

    private static final double RADIUS = 3.5;
    private static final double TRIGGER_RADIUS = 3.5;
    private static final double STUN_DURATION = 3;

    public void execute(Player player, Event genericEvent) {
        LivingEntity target = AbilityUtility.getTargetEntity(player, TRIGGER_RADIUS);
        if (target == null) return;
        for (LivingEntity entity : AbilityUtility.entitiesInRadius(AbilityUtility.getEntityMiddle(target), RADIUS)) {
            dealDamage(player, entity);
            summonParticles(entity.getLocation());
        }
        playSound(player);
        createConeWave(player);
    }

    @Override
    public boolean canActivate(Player player, Event genericEvent) {
        return AbilityUtility.getTargetEntity(player, TRIGGER_RADIUS) != null;
    }

    private void dealDamage(Player player, LivingEntity entity) {
        StunEffectManager.addStunnedEntity(entity, STUN_DURATION);
        StatSystem.getInstance().getStats(entity).addModifier("shieldSlowness",
                CustomStat.SPEED, 0, -10, 100);

        // knockback
        CombatSystem.dealDamage(entity, player, 0);
    }

    private void playSound(Player player) {
        player.playSound(player, Sound.ENTITY_ENDER_DRAGON_FLAP, 0.5F, 1);
        player.playSound(player, Sound.ITEM_SHIELD_BLOCK, 1.0F, 1.0F);
        player.playSound(player, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 0.5F, 1.0F);
    }

    private void summonParticles(Location location) {
        Objects.requireNonNull(location.getWorld()).spawnParticle(Particle.EXPLOSION_LARGE, location, 1);
        location.getWorld().spawnParticle(Particle.CRIT_MAGIC, location, 20, 0.5, 0.5, 0.5, 0.1);
    }

    private void createConeWave(Player player) {
        Vector direction = player.getLocation().getDirection().normalize();
        Vector origin = player.getLocation().toVector().add(new Vector(0, 0.5, 0));

        new BukkitRunnable() {
            double distance = 0;
            final double maxDistance = 5;
            final double angleSpread = Math.PI / 4;
            final int particlesPerStep = 3;

            @Override
            public void run() {
                if (distance > maxDistance) {
                    cancel();
                    return;
                }
                for (int i = 0; i < particlesPerStep; i++) {
                    double randomAngle = (Math.random() * angleSpread * 2) - angleSpread;
                    Vector spreadDirection = direction.clone().rotateAroundY(randomAngle);
                    Vector currentPos = origin.clone().add(spreadDirection.multiply(distance));
                    player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, currentPos.getX(),
                            player.getLocation().getY(), currentPos.getZ(), 0, 0, 0, 0, 0);
                }
                distance += 0.5;
            }
        }.runTaskTimer(AtheriumPlugin.getInstance(), 0, 1);
    }
}
