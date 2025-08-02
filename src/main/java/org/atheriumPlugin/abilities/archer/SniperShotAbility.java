package org.atheriumPlugin.abilities.archer;

import org.atheriumPlugin.AtheriumPlugin;
import org.atheriumPlugin.abilities.AbilityBase;
import org.atheriumPlugin.combat.CombatSystem;
import org.atheriumPlugin.stats.CustomStat;
import org.atheriumPlugin.stats.StatSystem;
import org.atheriumPlugin.utility.AbilityUtility;
import org.atheriumPlugin.utility.StunEffectManager;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class SniperShotAbility extends AbilityBase {

    private static final long DURATION = 30;
    private static final double SHOT_TRIGGER_RADIUS = 3;
    private static final double BULLET_SPEED = 2.75;
    private static final double MAX_DAMAGE_DISTANCE = 20;
    private static final int BULLET_AMOUNT = 3;
    private static final long SHOOT_INTERVAL = 7;

    @Override
    public void execute(Player player, Event genericEvent) {
        new BukkitRunnable() {
            int shotsFired = 0;

            @Override
            public void run() {
                if (shotsFired >= BULLET_AMOUNT) {
                    cancel();
                    return;
                }

                player.playSound(player, Sound.ITEM_TRIDENT_RIPTIDE_1, 1F, 1.25F);
                Vector direction = player.getLocation().getDirection().multiply(BULLET_SPEED);
                Location start = AbilityUtility.getEntityMiddle(player).add(0, 0.25, 0);
                shoot(player, start, direction);

                shotsFired++;
            }
        }.runTaskTimer(AtheriumPlugin.getInstance(), 0L, SHOOT_INTERVAL);
    }

    private void shoot(Player player, Location start, Vector direction) {
        new BukkitRunnable() {

            long remainingDuration = DURATION;
            final Location bulletPos = start.clone();

            @Override
            public void run() {
                if (remainingDuration < 0) {
                    cancel();
                    return;
                }
                remainingDuration--;
                if (checkCollisionAndTriggerDamage(bulletPos, player, start)) {
                    cancel();
                    return;
                }
                spawnBulletTrail(bulletPos);
                bulletPos.add(direction);
            }
        }.runTaskTimer(AtheriumPlugin.getInstance(), 0, 1);
    }

    private boolean checkCollisionAndTriggerDamage(Location bulletPos, Player player, Location start) {
        World world = bulletPos.getWorld();
        if (world == null) return false;

        List<LivingEntity> entities = AbilityUtility.entitiesInRadius(bulletPos, SHOT_TRIGGER_RADIUS);
        boolean inBlock = world.getBlockAt(bulletPos).getType().isSolid();

        if (!entities.isEmpty() || inBlock) {
            player.playSound(player, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 0.1F, 1);
            triggerDamage(bulletPos, player, start);
            return true;
        }
        return false;
    }

    private void spawnBulletTrail(Location location) {
        World world = location.getWorld();
        assert world != null;
        world.spawnParticle(Particle.CRIT, location, 1, 0, 0, 0, 0);
        world.spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0,
                new Particle.DustOptions(Color.fromRGB(228, 192, 124), 1f));
        world.spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0,
                new Particle.DustOptions(Color.fromRGB(255, 150, 120), 1f));
    }

    private void triggerDamage(Location bulletPos, Player player, Location start) {
        LivingEntity target = findClosestTarget(bulletPos);
        Location center = bulletPos;
        if (target != null) {
            center = AbilityUtility.getEntityMiddle(target);
            applyDamage(target, start, center, player);
            summonStarPolygon(target, player);
            player.playSound(player, Sound.BLOCK_DRIPSTONE_BLOCK_BREAK, 0.5F, 0.75F);
        }

        summonHitParticles(center);
    }

    private void applyDamage(LivingEntity target, Location start, Location center, Player player) {

        player.setVelocity(player.getLocation().getDirection().normalize().
                multiply(-1 * (MAX_DAMAGE_DISTANCE / start.distance(center) + 3) / MAX_DAMAGE_DISTANCE));

        double damage = StatSystem.getInstance().getStats(player).getStat(CustomStat.DAMAGE);
        double finalDamage = damage * (1 + start.distance(center) / MAX_DAMAGE_DISTANCE);
        CombatSystem.dealDamage(target, player, finalDamage);
        StunEffectManager.addStunnedEntity(target, 3);
    }

    private void summonHitParticles(Location center) {
        World world = center.getWorld();
        if (world != null) {
            world.spawnParticle(Particle.FLAME, center, 10, 0.75, 0.75, 0.75, 0);
            world.spawnParticle(Particle.SPELL_INSTANT, center, 10, 0.75, 0.75, 0.75, 0);
            world.spawnParticle(Particle.CRIT, center, 15, 0.75, 0.75, 0.75, 0);
        }
    }

    private LivingEntity findClosestTarget(Location bulletPos) {
        double minDistance = Double.MAX_VALUE;
        LivingEntity target = null;
        for (LivingEntity entity : AbilityUtility.entitiesInRadius(bulletPos, SHOT_TRIGGER_RADIUS)) {
            double normalizedDistance = entity.getLocation().distance(bulletPos) / SHOT_TRIGGER_RADIUS;
            if (normalizedDistance < minDistance) {
                minDistance = normalizedDistance;
                target = entity;
            }
        }
        return target;
    }

    public void summonStarPolygon(LivingEntity target, Player player) {
        int points = 5;
        double size = target.getHeight() * 0.9;
        World world = player.getWorld();
        Location[] edges = new Location[points];
        Location targetLocation = target.getEyeLocation().subtract(0, target.getHeight() / 5, 0);

        double yaw = Math.toRadians(player.getLocation().getYaw());
        Vector upVector = new Vector(0, 1, 0);
        Vector rightVector = new Vector(Math.cos(yaw), 0, Math.sin(yaw)).normalize();

        for (int i = 0; i < points; i++) {
            double angle = Math.toRadians(360.0 / points * i + 90);
            double x = Math.cos(angle) * size;
            double z = Math.sin(angle) * size;
            Vector pointVector = rightVector.clone().multiply(x)
                    .add(upVector.clone().multiply(z));
            edges[i] = targetLocation.clone().add(pointVector);
        }

        for (int i = 0; i < points; i++) {
            int nextIndex = (i + 2) % points;
            List<Vector> positions = AbilityUtility.generateLine(
                    edges[i].toVector(), edges[nextIndex].toVector(), 0.225);
            int len = positions.size();
            for (int j = 0; j < len; j++) {
                if (j > len / 3 && j < len / 3 * 2) continue;
                spawnStarParticles(positions.get(j).toLocation(world));
            }
        }
    }

    private void spawnStarParticles(Location location) {
        World world = location.getWorld();
        assert world != null;
        world.spawnParticle(Particle.CRIT, location, 1, 0, 0, 0, 0);
        world.spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0,
                new Particle.DustOptions(Color.fromRGB(228, 192, 124), 1.15f));

        if (Math.random() < 0.2) {
            world.spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0,
                    new Particle.DustOptions(Color.fromRGB(255, 150, 120), 1.15f));
            world.spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0,
                    new Particle.DustOptions(Color.fromRGB(255, 255, 255), 1.15f));
        }
    }
}
