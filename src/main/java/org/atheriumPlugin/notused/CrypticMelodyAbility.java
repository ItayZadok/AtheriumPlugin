//package org.atheriumPlugin.notused;
//
//import org.atherium.combatplugin.CombatPlugin;
//import org.atherium.combatplugin.abilityPlugin.utility.AbilityUtility;
//import org.bukkit.*;
//import org.bukkit.entity.Entity;
//import org.bukkit.entity.LivingEntity;
//import org.bukkit.entity.Player;
//import org.bukkit.event.Event;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.entity.EntityDamageByEntityEvent;
//import org.bukkit.event.entity.EntityDeathEvent;
//import org.bukkit.event.player.PlayerItemHeldEvent;
//import org.bukkit.scheduler.BukkitRunnable;
//import org.bukkit.scheduler.BukkitTask;
//import org.bukkit.util.Vector;
//
//import java.util.*;
//
//public class CrypticMelodyAbility extends AbilityBase {
//
//    private static final double MIN_DURATION = 10; // seconds
//    private static final double KILL_DURATION_INCREASE = 2; // seconds
//    private static final double MAX_DURATION = 20; // seconds
//    private static final double NOTE_DETECTION_WINDOW = 0.25; // Allows note detection within a small window of time
//
//    private static final Sound[] discSounds = new Sound[]{
//            Sound.MUSIC_DISC_WARD,
//            Sound.MUSIC_DISC_PIGSTEP,
//            Sound.MUSIC_DISC_MELLOHI
//    };
//
//    private final Map<UUID, PlayerState> playerStates = new WeakHashMap<>();
//
//    @Override
//    public void execute(Player player, Event genericEvent) {
//        stop(player);
//        playDisc(discSounds[(int) (Math.random() * discSounds.length)], player);
//    }
//
//    private void playDisc(Sound discSound, Player player) {
//        player.playSound(player, discSound, (float) 0.5, 1);
//        // init a playerState
//        UUID playerId = player.getUniqueId();
//        PlayerState state = new PlayerState(MIN_DURATION, System.currentTimeMillis(), discSound);
//        playerStates.put(playerId, state);
//
//        state.setPeriodic(
//                new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        PlayerState playerState = playerStates.get(playerId);
//                        double duration = playerState.getDuration();
//                        if (!player.isOnline() || duration > playerState.getMaxDuration()) {
//                            stop(player);
//                            cancel();
//                            return;
//                        }
//
//                        // replay the start because the end is off
//                        if (playerState.getCurrentDisc().equals(discSounds[0])) {
//                            if (Math.abs(duration % 6) < 0.025) {
//                                player.stopAllSounds();
//                                player.playSound(player, discSound, (float) 0.5, 1);
//                            }
//                        }
//
//                        generateNormalParticles(AbilityUtility.getEntityMiddle(player));
//
//                        if (detectDiscNotes(playerState.getCurrentDisc(), duration, 0.025)) {
//                            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 1);
//                            generateSpecialParticles(AbilityUtility.getEntityMiddle(player));
//                        }
//                    }
//                }.runTaskTimer(CombatPlugin.getInstance(), 0, 1)
//        );
//    }
//
//    private void generateNormalParticles(Location location) {
//        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.BLACK, 1f);
//        location.getWorld().spawnParticle(Particle.REDSTONE, location, 3,
//                0.5, 0.5, 0.5, 0, dustOptions);
//    }
//
//    private void generateSpecialParticles(Location location) {
//        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 1f);
//        location.getWorld().spawnParticle(Particle.REDSTONE, location, 10,
//                0.3, 0.3, 0.3, 0, dustOptions);
//    }
//
//    @EventHandler
//    public void onPlayerKill(EntityDeathEvent event) {
//        if (playerStates.isEmpty()) return;
//        Player player = event.getEntity().getKiller();
//        UUID playerId = player.getUniqueId();
//        PlayerState state = playerStates.get(playerId);
//        if (state != null) {
//            state.increaseMaxDuration(KILL_DURATION_INCREASE, MAX_DURATION);
//        }
//    }
//
//    @EventHandler
//    public void onSwitchItem(PlayerItemHeldEvent event) {
//        Player player = event.getPlayer();
//        if (playerStates.get(player.getUniqueId()) == null) return;
//
//        Material newItem = null;
//        if (player.getInventory().getItem(event.getNewSlot()) != null) {
//            newItem = Objects.requireNonNull(player.getInventory().getItem(event.getNewSlot())).getType();
//        }
//
//        if (newItem == null || !newItem.toString().toLowerCase().contains("hoe")) {
//            stop(player);
//        }
//    }
//
//    @EventHandler
//    public void onPlayerDamage(EntityDamageByEntityEvent event) {
//        if (!(event.getDamager() instanceof Player player)) return;
//        UUID playerId = player.getUniqueId();
//        PlayerState state = playerStates.get(playerId);
//        if (state == null) return;
//
//        if (detectDiscNotes(state.getCurrentDisc(), state.getDuration(), NOTE_DETECTION_WINDOW)) {
//            event.setDamage(event.getFinalDamage() * 2);
//            generateHitParticles(player, event.getEntity());
//        }
//    }
//
//    private void generateHitParticles(Player player, Entity entity) {
//        if (!(entity instanceof LivingEntity target)) return;
//
//        World world = player.getWorld();
//        Vector direction = player.getLocation().getDirection().clone();
//        direction.rotateAroundY(Math.random() < 0.5 ? 90 : -90).normalize();
//
//        double x = -direction.getX();
//        double z = -direction.getZ();
//        double yOffset = -0.5 + Math.random() * 1.5;
//
//        Vector targetMid = AbilityUtility.getEntityMiddle(target).toVector();
//        targetMid.add(new Vector(0, Math.sin(Math.PI / 2 * player.getLocation().getDirection().getY()), 0));
//        targetMid.add(new Vector(0, player.getLocation().toVector().subtract(target.getLocation().toVector()).normalize().getY() * 2, 0));
//
//        double distance = 1.25 * target.getHeight() / 2 * (1 + Math.random() * 0.2);
//        Vector start = targetMid.clone().add(new Vector(-distance * x, 0, -distance * z));
//        Vector end = targetMid.clone().add(new Vector(distance * x, 0, distance * z));
//
//        start.add(new Vector(0, yOffset, 0));
//        end.add(new Vector(0, -yOffset, 0));
//
//        Vector curveControlPoint = AbilityUtility.findCurveControlPoint(start, end, direction, true);
//        List<Vector> points = AbilityUtility.drawCurve(start, end, curveControlPoint, 0.05);
//
//        int mid = points.size() / 2;
//        double minValue = 0.5, maxValue = 1.5;
//
//        for (int i = 0; i < points.size(); i++) {
//            Location pos = points.get(i).toLocation(world);
//            double normalizedDistance = Math.abs(i - mid) / (double) mid;
//            double particleSize = (1.2 - normalizedDistance) * (maxValue - minValue) + minValue;
//
//            Particle.DustOptions dust = new Particle.DustOptions(Color.MAROON, (float) particleSize);
//            world.spawnParticle(Particle.REDSTONE, pos, 1, dust);
//        }
//    }
//
//    private void stop(Player player) {
//        UUID playerId = player.getUniqueId();
//        if (playerStates.get(playerId) == null) return;
//        playerStates.get(playerId).stopPeriodic();
//        playerStates.remove(playerId);
//        player.stopAllSounds();
//    }
//
//    private boolean detectDiscNotes(Sound disc, double duration, double detectionWindow) {
//        return switch (disc) {
//            case MUSIC_DISC_WARD -> isInDetectionWindow(duration, 1.1, detectionWindow);
//            case MUSIC_DISC_PIGSTEP -> isInDetectionWindow(duration, 0.6, detectionWindow);
//            case MUSIC_DISC_MELLOHI -> isInDetectionWindow(duration, 0.65, detectionWindow);
//            default -> false;
//        };
//    }
//
//    private boolean isInDetectionWindow(double time, double interval, double detectionWindow) {
//        double modTime = time % interval;
//        return modTime <= detectionWindow || modTime >= interval - detectionWindow;
//    }
//
//    private static class PlayerState {
//        private double maxDuration;
//        private final double startTime;
//        private final Sound currentDisc;
//        private BukkitTask periodic;
//
//        public PlayerState(double maxDuration, double startTime, Sound currentDisc) {
//            this.maxDuration = maxDuration;
//            this.startTime = startTime;
//            this.currentDisc = currentDisc;
//        }
//
//        public void stopPeriodic() {
//            periodic.cancel();
//        }
//
//        public double getMaxDuration() {
//            return maxDuration;
//        }
//
//        public Sound getCurrentDisc() {
//            return currentDisc;
//        }
//
//        public double getDuration() {
//            return (System.currentTimeMillis() - this.startTime) / 1000;
//        }
//
//        public void setPeriodic(BukkitTask periodic) {
//            this.periodic = periodic;
//        }
//
//        public void increaseMaxDuration(double increase, double maxCap) {
//            this.maxDuration = Math.min(maxDuration + increase, maxCap);
//        }
//    }
//}
