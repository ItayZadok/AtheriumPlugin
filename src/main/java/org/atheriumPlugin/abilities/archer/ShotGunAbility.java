package org.atheriumPlugin.abilities.archer;

import org.atheriumPlugin.AtheriumPlugin;
import org.atheriumPlugin.abilities.AbilityBase;
import org.atheriumPlugin.combat.CombatSystem;
import org.atheriumPlugin.stats.CustomStat;
import org.atheriumPlugin.stats.StatSystem;
import org.atheriumPlugin.utility.AbilityUtility;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShotGunAbility extends AbilityBase {

    private static final long DURATION = 30;
    private static final double BULLET_SPEED = 2;
    private static final double DAMAGE_TRIGGER_RADIUS = 3;
    private static final int BULLET_AMOUNT = 3;
    private static final double PIERCE_TIMES = 3;

    private static final Particle.DustOptions BLUE_PARTICLE = new Particle.DustOptions(
            Color.fromRGB(0, 204, 255), 0.8f);

    @Override
    public void execute(Player player, Event genericEvent) {
        if (!(genericEvent instanceof EntityShootBowEvent event)) return;
        event.setCancelled(true);
        event.getProjectile().remove();
        Vector direction = player.getLocation().getDirection().multiply(BULLET_SPEED);
        Location start = AbilityUtility.getEntityMiddle(player).add(0, 0.25, 0);
        player.playSound(player, Sound.ITEM_TRIDENT_RIPTIDE_1, 1F, 0.5F);
        for (double angle = (double) -BULLET_AMOUNT / 2; angle < (double) BULLET_AMOUNT / 2; angle++) {
            new BulletTask(start, direction.clone().rotateAroundY(Math.toRadians(angle * 6)), player)
                    .runTaskTimer(AtheriumPlugin.getInstance(), 0, 1);
        }
    }

    private class BulletTask extends BukkitRunnable {
        private final World world;
        private final Location bulletPos;
        private long duration = DURATION;
        private final Vector direction;
        private final Location start;
        private final Player player;
        private final List<UUID> enemiesPierced = new ArrayList<>();

        public BulletTask(Location start, Vector direction, Player player) {
            this.bulletPos = start.clone();
            this.world = bulletPos.getWorld();
            this.direction = direction;
            this.start = start;
            this.player = player;
        }

        @Override
        public void run() {
            boolean inBlock = world.getBlockAt(bulletPos).getType().isSolid();
            if (duration < 0 || inBlock) {
                cancel();
                return;
            }
            duration--;
            List<LivingEntity> entities = AbilityUtility.entitiesInRadius(bulletPos, DAMAGE_TRIGGER_RADIUS);
            if (!entities.isEmpty()) {
                double minDistance = Double.MAX_VALUE;
                LivingEntity target = null;
                for (LivingEntity entity : entities) {
                    if (enemiesPierced.contains(entity.getUniqueId())) continue;
                    double normalizedDistance = entity.getLocation().distance(bulletPos) / DAMAGE_TRIGGER_RADIUS;
                    if (normalizedDistance < minDistance) {
                        minDistance = normalizedDistance;
                        target = entity;
                    }
                }
                if (target != null) {
                    double damage = StatSystem.getInstance().getStats(player).getStat(CustomStat.DAMAGE);
                    CombatSystem.dealDamage(target, player, damage);

                    world.spawnParticle(Particle.SPELL_INSTANT,
                            AbilityUtility.getEntityMiddle(target), 5, 0.5, 0.5, 0.5);
                    enemiesPierced.add(target.getUniqueId());
                }
                if (enemiesPierced.size() >= PIERCE_TIMES) {
                    cancel();
                    return;
                }
            }
            spawnBulletParticles(world, bulletPos, direction);
            bulletPos.add(direction);
        }
    }

    private void spawnBulletParticles(World world, Location bulletPos, Vector direction) {
        world.spawnParticle(Particle.SCULK_SOUL, bulletPos.clone()
                .add(direction.clone().multiply(BULLET_SPEED)), 1, 0, 0, 0, 0);
        world.spawnParticle(Particle.SOUL_FIRE_FLAME, bulletPos.clone()
                .add(direction.clone().multiply(BULLET_SPEED)), 1, 0, 0, 0, 0);
        world.spawnParticle(Particle.SCRAPE, bulletPos.clone()
                .add(direction.clone().multiply(BULLET_SPEED)), 1, 0, 0, 0, 0);
        world.spawnParticle(Particle.CRIT_MAGIC, bulletPos.clone()
                .subtract(direction.clone().multiply(BULLET_SPEED)), 1, 0, 0, 0, 0);
        world.spawnParticle(Particle.REDSTONE, bulletPos, 1, 0, 0, 0, 0, BLUE_PARTICLE);
    }
}
