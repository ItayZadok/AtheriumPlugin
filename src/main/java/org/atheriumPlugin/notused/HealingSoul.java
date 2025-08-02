//package org.atheriumPlugin.notused;
//
//import org.atherium.combatplugin.abilityPlugin.abilities.AbilityBase;
//import org.bukkit.Location;
//import org.bukkit.Particle;
//import org.bukkit.entity.Player;
//import org.bukkit.event.Event;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.entity.EntityDeathEvent;
//import org.bukkit.event.player.PlayerInteractEvent;
//import org.bukkit.util.Vector;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//public class VengeanceSoulAbility extends AbilityBase {
//
//    private static final double DROP_CHANCE = 0.5; // 50% chance to drop blood orb
//    private static final int MAX_BLOOD_ORBS = 3;
//    private static final double HEAL_AMOUNT = 4.0;
//    private static final double PICK_RADIUS = 3.5;
//    private static final int HEART_PARTICLE_AMOUNT = 5;
//    private static final List<BloodOrb> activeBloodOrbs = new ArrayList<>();
//
//    @Override
//    public void execute(Player player, Event genericEvent) {
//        if (!(genericEvent instanceof EntityDeathEvent event)) return;
//
//        if (Math.random() <= DROP_CHANCE && activeBloodOrbs.size() < MAX_BLOOD_ORBS) {
//            createOrb(event.getEntity().getLocation(), player);
//        }
//    }
//
//    @EventHandler
//    public void onRightClick(PlayerInteractEvent event) {
//        if (activeBloodOrbs.isEmpty()) return;
//
//        Player player = event.getPlayer();
//        Location playerLocation = player.getEyeLocation();
//        Vector direction = playerLocation.getDirection().normalize();
//
//        for (BloodOrb bloodOrb : activeBloodOrbs) {
//            double distance = bloodOrb.getLocation().distance(playerLocation);
//            if (distance > PICK_RADIUS) continue;
//
//            Vector orbDirection = bloodOrb.getLocation().toVector().subtract(playerLocation.toVector()).normalize();
//            if (direction.dot(orbDirection) <= 0.95) continue;
//
//            if (bloodOrb.getLocation().distance(playerLocation.add(direction.multiply(distance))) < 0.3) {
//                generateHealParticles(bloodOrb.getLocation().add(new Vector(0, 1, 0)));
//                bloodOrb.destroy();
//                healPlayer(player);
//                return;
//            }
//        }
//    }
//
//    private void healPlayer(Player player) {
//        double newHealth = Math.min(player.getHealth() + HEAL_AMOUNT, player.getMaxHealth());
//        player.setHealth(newHealth);
//    }
//
//    private void generateHealParticles(Location location) {
//        for (int i = 0; i < HEART_PARTICLE_AMOUNT; i++) {
//            Objects.requireNonNull(location.getWorld()).spawnParticle(Particle.HEART, location, 1,
//                    Math.random() * 0.75,
//                    Math.random() * 0.75,
//                    Math.random() * 0.75, 0);
//        }
//    }
//
//    private void createOrb(Location location, Player player) {
//        int sum = 0;
//        for (BloodOrb orb : activeBloodOrbs) {
//            if (orb.isOwner(player)) sum++;
//            if (sum >= MAX_BLOOD_ORBS) return;
//        }
//
//        activeBloodOrbs.add(new BloodOrb(location, player));
//    }
//
//    public static void removeOrb(BloodOrb orb) {
//        activeBloodOrbs.remove(orb);
//    }
//}
