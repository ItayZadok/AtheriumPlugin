package org.atheriumPlugin.combat;

import org.atheriumPlugin.utility.AbilityUtility;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Manages particle effects for combat.
 * Separated from combat logic to follow Single Responsibility Principle.
 */
public class ParticleEffectManager {
    
    private static final double SMALL_HIT_ROTATION = 0.5;
    private static final double SMALL_HIT_RANGE = 0.5;
    private static final double SMALL_HIT_PARTICLE_DENSITY = 0.05;
    
    /**
     * Spawn critical hit particles
     */
    public void spawnCriticalHitParticles(Player player, Entity entity) {
        if (!(entity instanceof LivingEntity target)) return;
        
        World world = player.getWorld();
        Vector start = target.getLocation().toVector().add(new Vector(0, 0.25, 0));
        Vector end = target.getEyeLocation().toVector().add(new Vector(0, 0.75, 0));
        Vector direction = player.getLocation().getDirection();
        start.subtract(new Vector(direction.getX() * 0.75, 0, direction.getZ() * 0.75));
        
        Vector curveControlPoint = AbilityUtility.findCurveControlPoint(start, end, direction, true);
        List<Vector> points = AbilityUtility.generateCurve(start, end, curveControlPoint, 0.1);
        
        for (int i = 0; i < points.size(); i++) {
            Location pos = points.get(i).toLocation(world);
            double particleSize = 0.5 + (double) i / points.size();
            Particle.DustOptions dust = new Particle.DustOptions(
                    Color.fromRGB(204, 0, 102), (float) particleSize);
            world.spawnParticle(Particle.REDSTONE, pos.getX(), pos.getY(), pos.getZ(),
                    1, 0, 0, 0, dust);
        }
    }
    
    /**
     * Spawn normal hit particles
     */
    public void spawnNormalHitParticles(Player player, Entity entity) {
        if (!(entity instanceof LivingEntity target)) return;
        
        World world = player.getWorld();
        Vector direction = player.getLocation().getDirection().clone();
        
        if ((int) (Math.random() * 2) == 0) {
            direction.rotateAroundY(90).normalize();
        } else {
            direction.rotateAroundY(-90).normalize();
        }
        
        double x = -direction.clone().getX();
        double z = -direction.clone().getZ();
        double yOffset = -0.5 + Math.random() * 1.5;
        
        Vector targetMid = AbilityUtility.getEntityMiddle(target).toVector();
        targetMid.add(new Vector(0,
                Math.sin(Math.PI / 2 * player.getLocation().getDirection().getY()), 0));
        targetMid.add(new Vector(0, player.getLocation().
                toVector().subtract(target.getLocation().toVector()).normalize().getY() * 2, 0));
        
        double distance = 1.25 * target.getHeight() / 2 * (1 + Math.random() * 0.2);
        Vector start = targetMid.clone().add(new Vector(-distance * x, 0, -distance * z));
        Vector end = targetMid.clone().add(new Vector(distance * x, 0, distance * z));
        
        start.add(new Vector(0, yOffset, 0));
        end.add(new Vector(0, -yOffset, 0));
        
        Vector curveControlPoint = AbilityUtility.findCurveControlPoint(start, end, direction, true);
        List<Vector> points = AbilityUtility.generateCurve(start, end, curveControlPoint, 0.05);
        
        int mid = points.size() / 2;
        double minValue = 0.5, maxValue = 1;
        
        for (int i = 0; i < points.size(); i++) {
            Location pos = points.get(i).toLocation(world);
            double normalizedDistance = Math.abs(i - mid) / (double) mid;
            double particleSize = (1.2 - normalizedDistance) * (maxValue - minValue) + minValue;
            
            Particle.DustOptions dust = new Particle.DustOptions(
                    Color.fromRGB(255, 255, 255), (float) particleSize);
            world.spawnParticle(Particle.REDSTONE, pos, 1, dust);
        }
    }
    
    /**
     * Spawn small hit particles
     */
    public void spawnSmallHitParticles(Entity entity) {
        if (!(entity instanceof LivingEntity target)) return;
        
        World world = target.getWorld();
        Vector offset = new Vector(-SMALL_HIT_ROTATION + Math.random() * SMALL_HIT_ROTATION * 2,
                SMALL_HIT_RANGE, -SMALL_HIT_ROTATION + Math.random() * SMALL_HIT_ROTATION * 2);
        Vector middle = AbilityUtility.getEntityMiddle(target).toVector();
        Vector start = middle.clone().add(offset);
        Vector end = middle.clone().subtract(offset);
        
        Particle.DustOptions dust = new Particle.DustOptions(
                Color.fromRGB(255, 255, 255), 1F);
        for (Vector vector : AbilityUtility.generateLine(start, end, SMALL_HIT_PARTICLE_DENSITY)) {
            Location pos = vector.toLocation(world);
            world.spawnParticle(Particle.REDSTONE,
                    pos.getX(), pos.getY(), pos.getZ(), 1, 0, 0, 0, dust);
        }
    }
} 