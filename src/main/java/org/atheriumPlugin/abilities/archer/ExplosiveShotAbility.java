package org.atheriumPlugin.abilities.archer;

import org.atheriumPlugin.AtheriumPlugin;
import org.atheriumPlugin.abilities.AbilityBase;
import org.atheriumPlugin.combat.CombatSystem;
import org.atheriumPlugin.stats.CustomStat;
import org.atheriumPlugin.stats.StatSystem;
import org.atheriumPlugin.utility.AbilityUtility;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;

public class ExplosiveShotAbility extends AbilityBase {

    private static final long DURATION = 40;
    private static final double EXPLOSION_TRIGGER_RADIUS = 1.75;
    private static final double BULLET_SPEED = 2;
    private static final double EXPLOSION_RADIUS = 3.75;
    private static final double EXPLOSION_PARTICLE_RADIUS = 3;
    private static final double PLAYER_VELOCITY_RANGE = 6.5;
    private static final double ROTATION_RATE = 0.6;

    @Override
    public void execute(Player player, Event genericEvent) {
        Vector direction = player.getLocation().getDirection().multiply(BULLET_SPEED);
        Location start = AbilityUtility.getEntityMiddle(player);
        World world = player.getWorld();
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_SHOOT, 0.5F, 1F);
        player.playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 0.25F, 3F);

        new BukkitRunnable() {
            double remainingDuration = DURATION;
            double rotationAngle = 0;
            Location bulletPos = start.clone();

            @Override
            public void run() {
                if (remainingDuration <= 0) {
                    player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, 0.5F, 0.25F);
                    cancel();
                    return;
                }
                remainingDuration--;
                boolean inBlock = world.getBlockAt(bulletPos).getType().isSolid();
                if (!AbilityUtility.entitiesInRadius(bulletPos, EXPLOSION_TRIGGER_RADIUS).isEmpty() || inBlock) {
                    bulletPos = adjustBulletPosition(bulletPos, inBlock, world);
                    triggerExplosion(bulletPos, player);
                    cancel();
                    return;
                }

                spawnBulletParticles(bulletPos, direction, rotationAngle);
                bulletPos.add(direction);
                rotationAngle += ROTATION_RATE;
            }
        }.runTaskTimer(AtheriumPlugin.getInstance(), 0, 1);
    }

    private Location adjustBulletPosition(Location bulletPos, boolean inBlock, World world) {
        if (inBlock) {
            return world.getBlockAt(bulletPos).getLocation().add(0.5, -EXPLOSION_PARTICLE_RADIUS / 2, 0.5);
        }
        return bulletPos.subtract(new Vector(0, EXPLOSION_PARTICLE_RADIUS, 0));
    }

    private void spawnBulletParticles(Location bulletPos, Vector direction, double rotationAngle) {
        spawnCirclingTrail(bulletPos, direction, rotationAngle);
        if (bulletPos.getWorld() == null) return;

        bulletPos.getWorld().spawnParticle(Particle.CRIT,
                bulletPos.clone().subtract(direction), 5, 0.1, 0.1, 0.1, 0);

        bulletPos.getWorld().spawnParticle(Particle.FLAME,
                bulletPos.clone().add(direction), 1, 0, 0, 0, 0);

        bulletPos.getWorld().spawnParticle(Particle.ELECTRIC_SPARK,
                bulletPos.clone().add(direction).add(direction), 1, 0, 0, 0, 0);
    }

    private void generateExplosiveParticleSphere(Location center) {
        List<Vector> points = AbilityUtility.generatePointSphere(center.toVector(),
                EXPLOSION_PARTICLE_RADIUS, 15, 15);
        for (Vector pos : points) {
            spawnExplosionParticles(center, pos);
        }
    }

    private void spawnExplosionParticles(Location center, Vector pos) {
        World world = Objects.requireNonNull(center.getWorld());
        world.spawnParticle(Particle.FLAME, pos.getX(), pos.getY(), pos.getZ(), 1, 0, 0, 0, 0.005);
        world.spawnParticle(Particle.ELECTRIC_SPARK, pos.getX(), pos.getY(), pos.getZ(), 1, 0, 0, 0, 0);
        if (Math.random() < 0.33) {
            world.spawnParticle(Particle.LAVA, pos.getX(), pos.getY(), pos.getZ(), 1, 0, 0, 0, 0);
        }
    }

    private void damageEntities(Location bulletPos, Player player) {
        for (LivingEntity entity : AbilityUtility.entitiesInRadius(bulletPos, EXPLOSION_RADIUS)) {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,
                    100, 10, false, false));

            double normalizedDistance = entity.getLocation().distance(bulletPos) / EXPLOSION_RADIUS;
            double damageMultiplier = 1 - Math.pow(normalizedDistance, 2);
            double damage = StatSystem.getInstance().getStats(player).getStat(CustomStat.DAMAGE);
            CombatSystem.dealDamage(
                    entity, player, damage * (0.3 + 0.7 * damageMultiplier)
            );
        }
    }

    private void triggerExplosion(Location bulletPos, Player player) {
        damageEntities(bulletPos, player);
        generateExplosiveParticleSphere(bulletPos);
        player.playSound(bulletPos, Sound.BLOCK_LAVA_POP, 2F, 1.2F);
        player.playSound(bulletPos, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 0.75F, 0.5F);

        double distance = player.getLocation().distance(bulletPos);
        if (distance <= PLAYER_VELOCITY_RANGE) {
            launchPlayer(player, bulletPos);
        } else {
            player.playSound(player.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 0.25F, 0.5F);
        }
    }

    private void launchPlayer(Player player, Location bulletPos) {
        Vector launchDirection = player.getLocation().toVector().subtract(bulletPos.toVector()).normalize();
        player.setVelocity(launchDirection.multiply(1.15));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 50, 2, false, false));
        spawnCircleParticles(player);
    }

    private void spawnCircleParticles(Player player) {
        new BukkitRunnable() {
            final double[] radius = {1.0, 1.2, 0.8};
            int step = 0;

            @Override
            public void run() {
                AbilityUtility.generatePointCircle(player.getLocation().toVector(), radius[step], 20)
                        .forEach(pos -> player.getWorld().spawnParticle(Particle.TOTEM,
                                pos.toLocation(player.getWorld()), 0));
                if (++step >= radius.length) cancel();

                player.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 0.25F, 0.5F);
            }
        }.runTaskTimer(AtheriumPlugin.getInstance(), 0, 5);
    }

    private void spawnCirclingTrail(Location bulletPos, Vector direction, double rotationAngle) {
        double radius = 0.2;
        double xOffset = radius * Math.cos(rotationAngle);
        double yOffset = radius * Math.sin(rotationAngle);
        double zOffset = radius * Math.sin(rotationAngle);
        Vector perpendicularX = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize();
        Vector perpendicularZ = direction.clone().crossProduct(perpendicularX).normalize();
        Location leftTop = bulletPos.clone()
                .subtract(perpendicularX.clone().multiply(xOffset))
                .add(perpendicularZ.clone().multiply(zOffset))
                .subtract(0, yOffset, 0);
        Location leftMiddle = bulletPos.clone()
                .subtract(perpendicularX.clone().multiply(zOffset * 3))
                .subtract(perpendicularZ.clone().multiply(xOffset * 3))
                .subtract(0, yOffset, 0);
        Location rightTop = bulletPos.clone()
                .add(perpendicularX.clone().multiply(xOffset * 3))
                .add(perpendicularZ.clone().multiply(zOffset * 3))
                .subtract(0, yOffset, 0);
        Location rightMiddle = bulletPos.clone()
                .add(perpendicularX.clone().multiply(zOffset * 3))
                .add(perpendicularZ.clone().multiply(xOffset * 3))
                .add(0, yOffset, 0);

        spawnTrailParticles(rightMiddle, direction); // right middle
        spawnTrailParticles(leftTop, direction); // left top
        spawnTrailParticles(leftMiddle, direction); // left middle
        spawnTrailParticles(rightTop, direction); // right top
    }

    private void spawnTrailParticles(Location trailPos, Vector direction) {
        if (trailPos.getWorld() == null) return;
        trailPos.getWorld().spawnParticle(Particle.SCRAPE, trailPos, 1, 0, 0, 0, 0);
        trailPos.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE,
                trailPos.clone().subtract(direction.clone().multiply(0.2)), 1, 0, 0, 0, 0);
    }
}
