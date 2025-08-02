//package org.atheriumPlugin.notused;
//
//import org.atherium.combatplugin.ItemPlugin.enums.CustomStat;
//import org.atherium.combatplugin.ItemPlugin.stats.StatEntity;
//import org.atherium.combatplugin.abilityPlugin.utility.AbilityUtility;
//import org.atherium.combatplugin.abilityPlugin.utility.GlowingEffectUtil;
//import org.atherium.combatplugin.abilityPlugin.utility.ItemBuilder;
//import org.bukkit.*;
//import org.bukkit.entity.EntityType;
//import org.bukkit.entity.ItemDisplay;
//import org.bukkit.entity.LivingEntity;
//import org.bukkit.entity.Player;
//import org.bukkit.util.RayTraceResult;
//import org.bukkit.util.Vector;
//
//import java.util.List;
//
//public class BloodOrb {
//
//    private static final int SLOW_RADIUS = 3;
//    private static final int ORB_LIFETIME = 400; // 20 seconds
//    private static final double MAX_DISTANCE = 20;
//    private final Player owner;
//    private final Location location;
//    private ItemDisplay orb;
//    private double duration = ORB_LIFETIME;
//
//    public BloodOrb(Location location, Player owner) {
//        this.location = createOrb(location);
//        this.owner = owner;
//    }
//
//    public void periodic() {
//        duration--;
//        if (duration < 0 || owner.getLocation().distance(location) > MAX_DISTANCE) destroy();
//        slowNearbyEnemies(location);
//
//        spawnBloodParticle(location);
//        if (duration % 2 == 0) spawnSkullParticle(location);
//    }
//
//    public void destroy() {
//        orb.remove();
//        HealingSoul.removeOrb(this);
//    }
//
//    private Location createOrb(Location location) {
//        World world = location.getWorld();
//        RayTraceResult result = world.rayTraceBlocks(location, new Vector(0, -1, 0), 10);
//        Location pos;
//        if (result.getHitBlock() == null)
//            pos = location;
//        else
//            pos = result.getHitPosition().toLocation(world);
//        pos.add(new Vector(0, 0.5, 0));
//        orb = (ItemDisplay) world.spawnEntity(pos, EntityType.ITEM_DISPLAY);
//        orb.setItemStack(new ItemBuilder(Material.PLAYER_HEAD)
//                .setCustom("http://textures.minecraft.net/texture/41b97679a6616971c58c7c840fea78df5574469eda91b53ef8058cacde0e020a")
//                .build()
//        );
//        orb.setGravity(false);
//        GlowingEffectUtil.setGlowing(orb, ChatColor.RED, true);
//        return pos;
//    }
//
//    private void spawnBloodParticle(Location location) {
//        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 1.0f);
//        location.getWorld().spawnParticle(Particle.REDSTONE, location, 3, 0.75, 0.15, 0.75, dustOptions);
//    }
//
//    private void spawnSkullParticle(Location location) {
//        location.getWorld().spawnParticle(Particle.FLAME, location.clone().add(new Vector(0, 0.35, 0)),
//                1, 0, 0, 0, 0);
//    }
//
//    private void slowNearbyEnemies(Location location) {
//        List<LivingEntity> nearbyEntities = AbilityUtility.entitiesInRadius(location, SLOW_RADIUS).
//                stream().limit(3).toList();
//        for (LivingEntity entity : nearbyEntities) {
//            StatEntity.getMobStats(entity).addModifier("ordSlow", CustomStat.SPEED,
//                    -10, 0, 20);
//        }
//    }
//
//    public boolean isOwner(Player player) {
//        return player.equals(owner);
//    }
//
//    public Location getLocation() {
//        return location;
//    }
//}
