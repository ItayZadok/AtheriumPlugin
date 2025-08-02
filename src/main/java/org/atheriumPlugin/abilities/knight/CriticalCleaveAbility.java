package org.atheriumPlugin.abilities.knight;

import org.atheriumPlugin.AtheriumPlugin;
import org.atheriumPlugin.abilities.AbilityBase;
import org.atheriumPlugin.combat.CombatSystem;
import org.atheriumPlugin.stats.CustomStat;
import org.atheriumPlugin.stats.StatSystem;
import org.atheriumPlugin.utility.AbilityUtility;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Objects;

public class CriticalCleaveAbility extends AbilityBase {

    private static final double DAMAGE_PERCENT = 0.6;
    private static final double RADIUS = 2.5;

    public void execute(Player player, Event genericEvent) {
        if (!(genericEvent instanceof EntityDamageByEntityEvent event)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        playSound(player);
        dealDamage(player, target);
        summonParticles(player, target);
    }

    @Override
    public boolean canActivate(Player player, Event genericEvent) {
        if (!(genericEvent instanceof EntityDamageByEntityEvent event)) return false;
        if (!(event.getEntity() instanceof LivingEntity target)) return false;
        return AbilityUtility.entitiesInRadius(AbilityUtility.getEntityMiddle(target), RADIUS).size() > 1;
    }

    private void playSound(Player player) {
        player.playSound(player, Sound.ENTITY_PLAYER_ATTACK_CRIT, 0.5F, 1.5F);
        player.playSound(player, Sound.ENTITY_GENERIC_EXPLODE, 0.5F, 0.1F);
    }

    private void summonParticles(Player player, LivingEntity target) {
        player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, AbilityUtility.getEntityMiddle(target), 1);
        spawnShockwaveEffect(target.getLocation().add(new Vector(0, 0.25, 0)));
        spawnDebrisEffect(player.getLocation());
    }

    private void dealDamage(Player player, LivingEntity target) {
        double damage = StatSystem.getInstance().getStats(player).getStat(CustomStat.DAMAGE) * DAMAGE_PERCENT;
        for (LivingEntity entity : AbilityUtility.entitiesInRadius(AbilityUtility.getEntityMiddle(target), RADIUS)) {
            if (entity.equals(target)) continue;
            CombatSystem.dealDamage(entity, player, damage);
            spawnLightExplosionEffect(entity);
        }
    }

    private void spawnShockwaveEffect(Location location) {
        new BukkitRunnable() {
            double radius = 0.5;
            final double maxRadius = 4.0;
            final double expandRate = 0.3;

            @Override
            public void run() {
                if (radius > maxRadius) {
                    cancel();
                    return;
                }

                int particles = 20; // Number of particles in the circle
                for (int i = 0; i < particles; i++) {
                    double angle = (2 * Math.PI * i) / particles;
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);
                    location.add(x, 0, z);
                    Objects.requireNonNull(location.getWorld()).spawnParticle(Particle.SCRAPE,
                            location, 1, 0, 0, 0, 0.1);
                    location.subtract(x, 0, z);
                }

                radius += expandRate;
            }
        }.runTaskTimer(AtheriumPlugin.getInstance(), 0, 2); // Spawns every 2 ticks
    }

    private void spawnDebrisEffect(Location location) {
        Objects.requireNonNull(location.getWorld()).spawnParticle(Particle.BLOCK_CRACK, location,
                25, 0.5, 0.1, 0.5, 0.05, Material.STONE.createBlockData());
    }


    private void spawnLightExplosionEffect(LivingEntity entity) {
        Location loc = entity.getLocation().clone().add(0, 1, 0);
        entity.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 10, 0.3, 0.3, 0.3, 0.1);
    }
}
