//package org.atheriumPlugin.notused;
//
//import org.atherium.combatplugin.CombatPlugin;
//import org.atherium.combatplugin.abilityPlugin.abilities.AbilityBase;
//import org.atherium.combatplugin.abilityPlugin.utility.AbilityUtility;
//import org.bukkit.*;
//import org.bukkit.entity.Entity;
//import org.bukkit.entity.LivingEntity;
//import org.bukkit.entity.Player;
//import org.bukkit.event.Event;
//import org.bukkit.util.Vector;
//
//import java.util.*;
//
//public class EmberWaveAbility extends AbilityBase {
//
//    private static final Particle.DustOptions RED_PARTICLE = new Particle.DustOptions(Color.RED, 1f);
//    private static final Particle.DustOptions ORANGE_PARTICLE
//            = new Particle.DustOptions(Color.fromRGB(255, 204, 102), 1f);
//
//    private static final double DURATION = 0.75; // seconds
//    private static final double FIRE_DURATION = 3; // seconds
//    private static final double RATE = 3;
//
//    private final Map<Player, PlayerState> playerStates = new HashMap<>();
//    private long i = 0;
//
//    @Override
//    public void execute(Player player, Event genericEvent) {
//        Location start = player.getLocation().add(new Vector(0, 0.25, 0));
//        playerStates.put(player, new PlayerState(player, start, System.currentTimeMillis()));
//    }
//
//    @Override
//    public void periodic() {
//        i++;
//        for (Player player : playerStates.keySet()) {
//            if (player == null || player.isDead() || !player.isOnline()) {
//                stop(player);
//                continue;
//            }
//
//            PlayerState state = playerStates.get(player);
//            if (state.hasEnded()) continue;
//
//            if (i % RATE == 0) {
//                state.update();
//            }
//        }
//    }
//
//    private void stop(Player player) {
//        if (playerStates.get(player) == null) return;
//        playerStates.remove(player);
//    }
//
//    private static class PlayerState {
//
//        private final double startTime;
//        private double scale = 0.5;
//
//        private final List<Wave> waveList = new ArrayList<>();
//
//        public PlayerState(Player player, Location start, double startTime) {
//            this.startTime = startTime;
//            this.waveList.add(new Wave(start.clone(), new Vector(0, 0, 1), player));
//            this.waveList.add(new Wave(start.clone(), new Vector(0, 0, -1), player));
//            this.waveList.add(new Wave(start.clone(), new Vector(1, 0, 0), player));
//            this.waveList.add(new Wave(start.clone(), new Vector(-1, 0, 0), player));
//        }
//
//        public void update() {
//            for (Wave wave : waveList) wave.update(scale);
//            scale += 0.15;
//        }
//
//        public boolean hasEnded() {
//            return (System.currentTimeMillis() - startTime) / 1000 >= DURATION;
//        }
//    }
//
//    private record Wave(Location pos, Vector direction, Player player) {
//
//        public void update(double scale) {
//            spawnWaveParticle(scale);
//            igniteNearbyEntities();
//            playSound();
//            pos.add(direction).add(direction);
//        }
//
//        private void igniteNearbyEntities() {
//            List<Entity> nearbyEntities = Objects.requireNonNull(pos.getWorld())
//                    .getNearbyEntities(pos, 1.5, 1.5, 1.5).stream()
//                    .filter(entity -> entity instanceof LivingEntity && !(entity instanceof Player))
//                    .toList();
//            for (Entity entity : nearbyEntities) {
//                if (!(entity instanceof LivingEntity livingEntity)) return;
//                igniteEntity(livingEntity);
//            }
//        }
//
//        private void spawnWaveParticle(double scale) {
//            Vector waveDirection = new Vector(-direction.getZ(), 0, direction.getX()).normalize().multiply(scale);
//            Vector midPoint1 = pos.toVector().add(waveDirection);
//            Vector midPoint2 = pos.toVector().subtract(waveDirection);
//            Vector sidePoint1 = midPoint1.clone().add(direction.clone().multiply(-0.5));
//            Vector sidePoint2 = midPoint2.clone().add(direction.clone().multiply(-0.5));
//
//            List<Vector> points = AbilityUtility.generateLine(midPoint1, midPoint2, 0.2);
//            points.addAll(AbilityUtility.generateLine(midPoint1, sidePoint1, 0.2));
//            points.addAll(AbilityUtility.generateLine(midPoint2, sidePoint2, 0.2));
//
//            World world = pos.getWorld();
//            assert world != null;
//            for (Vector point : points) {
//                world.spawnParticle(Particle.REDSTONE, point.toLocation(world), 1, RED_PARTICLE);
//                world.spawnParticle(Particle.REDSTONE, point.toLocation(world).add(new Vector(0, 0.25, 0)),
//                        1, ORANGE_PARTICLE);
//                world.spawnParticle(Particle.FLAME, point.toLocation(world).add(new Vector(0, 0.5, 0)),
//                        1, 0, 0, 0, 0);
//            }
//        }
//
//        private void igniteEntity(LivingEntity entity) {
//            entity.setVisualFire(true);
//            Bukkit.getScheduler().runTaskLater(CombatPlugin.getInstance(),
//                    () -> entity.setVisualFire(false), (long) FIRE_DURATION * 20);
//        }
//
//        private void playSound() {
//            player.playSound(player, Sound.ENTITY_BLAZE_SHOOT, 0.1F, 0.5F);
//            player.playSound(player, Sound.ENTITY_BLAZE_SHOOT, 0.15F, 2F);
//            player.playSound(player, Sound.BLOCK_LAVA_POP, 0.25F, 1F);
//            player.playSound(player, Sound.ENTITY_BLAZE_HURT, 0.25F, 1F);
//        }
//    }
//}