package org.atheriumPlugin.abilities.archer;

import org.atheriumPlugin.AtheriumPlugin;
import org.atheriumPlugin.abilities.AbilityBase;
import org.atheriumPlugin.stats.CustomStat;
import org.atheriumPlugin.stats.StatSystem;
import org.atheriumPlugin.utility.AbilityUtility;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;

public class DoubleJumpAbility extends AbilityBase {

    private static final int DURATION = 20;
    private static final long RATE = 5;
    private static final double LARGE_RADIUS = 1.5;
    private static final double SPEED_DURATION = 3;
    private static final double SMALL_RADIUS = 0.5;

    private static final HashMap<Player, PlayerState> playerStates = new HashMap<>();

    @Override
    public void execute(Player player, Event genericEvent) {
        playerStates.put(player, new PlayerState(player));
    }

    @Override
    public void periodic() {
        playerStates.entrySet().removeIf(entry -> {
            Player player = entry.getKey();
            if (player == null || player.isDead() || !player.isOnline()) {
                playerStates.get(player).stop();
                return true;
            }
            entry.getValue().update();
            return false;
        });
    }

    @EventHandler
    public void onSwitchGameMode(PlayerGameModeChangeEvent event) {
        if (!event.getNewGameMode().equals(GameMode.CREATIVE)) {
            Bukkit.getScheduler().runTaskLater(AtheriumPlugin.getInstance(),
                    () -> event.getPlayer().setAllowFlight(true), 1);
        }
    }

    private static class PlayerState {

        private final Player player;
        private long duration = DURATION;

        public PlayerState(Player player) {
            this.player = player;
            generateCircle(player, LARGE_RADIUS);
            applyDoubleJumpVelocity(player);

            player.playSound(player, Sound.BLOCK_BEACON_ACTIVATE, 1F, 0.75F);
            StatSystem.getInstance().getStats(player).addModifier("DoubleJumpSpeed",
                    CustomStat.SPEED, 10, 0, (long) SPEED_DURATION * 20);
        }

        public void update() {
            duration--;
            if (duration % RATE != 0) return;
            if (duration < 0 || player.isDead() || player.isOnGround()) {
                if (!player.isDead()) {
                    generateCircle(player, LARGE_RADIUS);
                }
                stop();
            } else {
                generateCircle(player, SMALL_RADIUS);
            }
        }

        private void stop() {
            playerStates.remove(player);
        }

        private void applyDoubleJumpVelocity(Player player) {
            Location loc = player.getLocation();
            loc.setPitch(0);
            Vector vec = loc.getDirection().setY(0.3);
            player.setVelocity(vec);
        }

        private void generateCircle(Player player, double radius) {
            player.playSound(player, Sound.ENTITY_PHANTOM_FLAP, 2, 2);
            Location center = player.getLocation().add(new Vector(0, 0.15, 0));
            World world = center.getWorld();
            assert world != null;
            List<Vector> points = AbilityUtility.generatePointCircle(center.toVector(), radius, (int) (18 * radius));
            for (int i = 0; i < points.size(); i++) {
                Location pos = points.get(i).toLocation(world);
                if (i % 2 == 0) {
                    world.spawnParticle(Particle.FIREWORKS_SPARK, pos, 1, 0, 0, 0, 0);
                } else {
                    world.spawnParticle(Particle.END_ROD, pos, 1, 0, 0, 0, 0);
                }
            }
        }
    }
}