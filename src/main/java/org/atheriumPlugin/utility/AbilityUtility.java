package org.atheriumPlugin.utility;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AbilityUtility {

    public static List<LivingEntity> entitiesInRadius(Location center, double range) {
        List<LivingEntity> entities = new ArrayList<>();
        List<Entity> nearbyEntities = (List<Entity>) Objects.requireNonNull(center.getWorld()).getNearbyEntities(center, range, range, range);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player) continue; // don't hit players
            if (!(entity instanceof LivingEntity livingEntity)) continue;
            if (livingEntity.getLocation().distance(center) > range) continue;
            entities.add(livingEntity);
        }
        return entities;
    }

    public static boolean isCritical(Player player) {
        return player.getFallDistance() > 0.0f && !player.isOnGround() && !player.isInWater() &&
                player.getPassengers().isEmpty() && player.getPotionEffect(PotionEffectType.BLINDNESS) == null
                && !player.isSprinting();
    }

    public static boolean isChargingShield(Player player) {
        return player.isHandRaised() && player.getInventory().getItemInOffHand().getType().equals(Material.SHIELD) ||
                player.getInventory().getItemInMainHand().getType().equals(Material.SHIELD);
    }

    public static List<Vector> generateLine(Vector point1, Vector point2, double spaceBetweenPoints) {
        List<Vector> points = new ArrayList<>();
        Vector p1 = point1.clone();
        Vector p2 = point2.clone();
        double distance = p1.distance(p2);
        Vector vector = p2.clone().subtract(p1).normalize().multiply(spaceBetweenPoints);
        double length = 0;
        for (; length < distance; p1.add(vector)) {
            points.add(p1.clone());
            length += spaceBetweenPoints;
        }
        return points;
    }

    public static List<Vector> generateCurve(Vector point1, Vector point2,
                                             Vector controlPoint, double spaceBetweenPoints) {
        List<Vector> points = new ArrayList<>();
        Vector p1 = point1.clone();
        Vector p2 = point2.clone();
        for (double t = 0; t <= 1; t += spaceBetweenPoints) {
            double u = 1 - t;
            double tt = t * t;
            double uu = u * u;

            Vector point = p1.clone().multiply(uu); // (1-t)^2 * P0
            point.add(controlPoint.clone().multiply(2 * u * t)); // + 2 * (1-t) * t * P1
            point.add(p2.clone().multiply(tt)); // + t^2 * P2
            points.add(point);
        }
        return points;
    }

    public static Vector findCurveControlPoint(Vector p1, Vector p2, Vector directionLocation, boolean reverse) {
        Vector mid1 = new Vector(p1.getX(), (p2.getY() + p1.getY()) / 2, p2.getZ());
        Vector mid2 = new Vector(p2.getX(), (p2.getY() + p1.getY()) / 2, p1.getZ());

        return reverse
                ? (mid1.distance(directionLocation) < mid2.distance(directionLocation) ? mid2 : mid1)
                : (mid1.distance(directionLocation) > mid2.distance(directionLocation) ? mid2 : mid1);
    }

    public static List<Vector> generatePointCircle(Vector center, double radius, int count) {
        List<Vector> points = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double angle = 2 * Math.PI * i / count;
            points.add(new Vector(
                    center.getX() + radius * Math.cos(angle), center.getY(),
                    center.getZ() + radius * Math.sin(angle))
            );
        }
        return points;
    }

    public static List<Vector> generatePointSphere(Vector center, double radius, double ySpace, double xSpace) {
        List<Vector> points = new ArrayList<>();
        for (double phi = 0; phi <= Math.PI; phi += Math.PI / ySpace) {
            double y = radius * Math.cos(phi) + radius;
            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / xSpace) {
                double x = radius * Math.cos(theta) * Math.sin(phi);
                double z = radius * Math.sin(theta) * Math.sin(phi);
                center.add(new Vector(x, y, z));
                points.add(center.clone());
                center.subtract(new Vector(x, y, z));
            }
        }
        return points;
    }

    public static void resetMobTarget(LivingEntity entity) {
        if (entity instanceof Mob mob) {
            mob.setTarget(null);
        }
    }

    public static Location getEntityMiddle(LivingEntity entity) {
        return entity.getLocation().add(new Vector(0, entity.getHeight() / 2, 0));
    }

    public static LivingEntity getTargetEntity(Player player, double radius) {
        var result = player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getLocation().getDirection(),
                radius,
                mob -> mob instanceof LivingEntity
                        && !mob.equals(player)
                        && !(mob instanceof BlockDisplay) // Exclude BlockDisplays
        );
        if (result == null || result.getHitEntity() == null) return null;
        return (LivingEntity) result.getHitEntity();
    }
}
