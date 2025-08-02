package org.atheriumPlugin.abilities.archer;

import org.atheriumPlugin.AtheriumPlugin;
import org.atheriumPlugin.stats.CustomStat;
import org.atheriumPlugin.stats.StatSystem;
import org.atheriumPlugin.utility.AbilityUtility;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;

public class Dagger {

    private static final double DAGGER_DURATION = 5;
    private static final double DAGGER_EFFECT_RADIUS = 3.5;

    private final Location location;
    private final World world;
    private final long creationTime;
    private ItemDisplay itemDisplay;
    private Vector add;
    private boolean destroyed = false;

    public Dagger(Location location, Vector add, Location center) {
        this.location = location;
        this.add = add;
        this.world = location.getWorld();
        generateDisplay(location, center);
        creationTime = System.currentTimeMillis();
        periodic();
    }

    private void periodic() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!add.equals(new Vector(0, 0, 0))) {
                    updateLocation(); // move dagger
                }
                assert world != null;
                if (world.getBlockAt(location.clone().subtract(new Vector(0, 1, 0))).getType().isSolid()) {
                    itemDisplay.teleport(location);
                    disableLocationUpdate();
                }
                for (LivingEntity entity : AbilityUtility.entitiesInRadius(location, DAGGER_EFFECT_RADIUS)) {
                    if (entity instanceof ArmorStand) continue;
                    StatSystem.getInstance().getStats(entity).addModifier("daggerSlow", CustomStat.SPEED,
                            -10, 0, 20);
                    StatSystem.getInstance().getStats(entity).addModifier("daggerWeaken", CustomStat.DEFENCE,
                            -10, 0, 20);
                    StatSystem.getInstance().getStats(entity).addModifier("daggerDebuff", CustomStat.DAMAGE,
                            0, -35, 20);
                }
                generateParticles(location.clone().add(new Vector(0, 0.5, 0)));
                if (System.currentTimeMillis() - creationTime > DAGGER_DURATION * 1000) {
                    destroy();
                    cancel();
                }
            }
        }.runTaskTimer(AtheriumPlugin.getInstance(), 0, 1);
    }

    private void generateDisplay(Location location, Location center) {
        itemDisplay = (ItemDisplay) location.getWorld().spawnEntity(location, EntityType.ITEM_DISPLAY);
        itemDisplay.setItemStack(new ItemStack(Material.NETHERITE_SWORD));
        itemDisplay.setGravity(false);
        Vector direction = center.toVector().subtract(location.toVector()).normalize();
        float yaw = (float) Math.toDegrees(Math.atan2(direction.getZ(), direction.getX()));
        float pitch = 0;

        location.setYaw(yaw);
        location.setPitch(pitch);

        Transformation transformation = itemDisplay.getTransformation();
        Quaternionf leftRotation = transformation.getLeftRotation();

        leftRotation.set(-1, 0, 0, 0);
        transformation.getLeftRotation().set(leftRotation);
        itemDisplay.setTransformation(transformation);
    }


    private void generateParticles(Location location) {
        World world = location.getWorld();
        assert world != null;
        world.spawnParticle(Particle.REDSTONE, location, 1, 0.75, 0.75, 0.75,
                new Particle.DustOptions(Color.GRAY, 1.5f));
    }

    public void updateLocation() {
        itemDisplay.teleport(location.add(add));
    }

    public void disableLocationUpdate() {
        add = new Vector(0, 0, 0);
    }

    public void destroy() {
        itemDisplay.remove();
        destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public Location getLocation() {
        return location;
    }
}