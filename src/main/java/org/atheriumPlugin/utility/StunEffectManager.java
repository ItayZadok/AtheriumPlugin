package org.atheriumPlugin.utility;

import org.atheriumPlugin.AtheriumPlugin;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StunEffectManager {

    private static final Map<LivingEntity, Long> stunnedEnemies = new ConcurrentHashMap<>();
    private final Particle.DustOptions stunParticle = new Particle.DustOptions(Color.fromRGB(255, 215, 0), 0.8f);

    public StunEffectManager() {
        double jumps = 16;
        new BukkitRunnable() {
            double cur = 0;

            @Override
            public void run() {
                cur += jumps;
                Iterator<Map.Entry<LivingEntity, Long>> iterator = stunnedEnemies.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<LivingEntity, Long> entry = iterator.next();
                    LivingEntity entity = entry.getKey();
                    long endTime = entry.getValue();
                    if (endTime - System.currentTimeMillis() < 0 || entity.isDead()) {
                        iterator.remove();
                        continue;
                    }
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2, 255));
                    double curRadians = Math.toRadians(cur % 360);
                    double jumpsRadians = Math.toRadians(jumps * 2);
                    spawnParticles(entity, curRadians);
                    spawnParticles(entity, curRadians + jumpsRadians);
                    spawnParticles(entity, curRadians - jumpsRadians);
                }
            }
        }.runTaskTimer(AtheriumPlugin.getInstance(), 0, 1);
    }

    public static void addStunnedEntity(LivingEntity entity, double timeInSeconds) {
        if (entity instanceof Player) return;
        if (stunnedEnemies.get(entity) != null && stunnedEnemies.get(entity) > System.currentTimeMillis() + timeInSeconds * 1000)
            return;
        stunnedEnemies.put(entity, (long) (System.currentTimeMillis() + timeInSeconds * 1000));
    }

    public static boolean isStunnedEntity(LivingEntity entity) {
        return stunnedEnemies.get(entity) != null;
    }

    private void spawnParticles(LivingEntity entity, double angle) {
        Location pos = entity.getEyeLocation().toVector()
                .add(new Vector(0.4, 0.3, 0.4).multiply(entity.getHeight() / 2).rotateAroundY(angle))
                .toLocation(entity.getWorld());
        entity.getWorld().spawnParticle(Particle.REDSTONE, pos.getX(), pos.getY(), pos.getZ(), 1, 0, 0, 0, stunParticle);
    }
}
