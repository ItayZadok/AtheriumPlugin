package org.atheriumPlugin.abilities.assassin;

import org.atheriumPlugin.abilities.AbilityBase;
import org.atheriumPlugin.stats.CustomStat;
import org.atheriumPlugin.stats.StatSystem;
import org.atheriumPlugin.utility.AbilityUtility;
import org.atheriumPlugin.utility.StunEffectManager;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.concurrent.ConcurrentHashMap;

public class BladeDance extends AbilityBase {

    private static final double SPEED_DURATION = 3; // seconds
    private static final double SPEED_BUFF = 20; // 1-100
    private static final double VULNERABILITY_DURATION = 1; // seconds
    private static final double LEVITATION_RANGE = 3;
    private static final double FINAL_KNOCKBACK_RANGE = 4;
    private static final double PARTICLE_RANGE = 0.75;
    private static final double DASH_RANGE = 1.2;

    private static final Particle.DustOptions RED_PARTICLE = new Particle.DustOptions(Color.RED, 1.25f);
    private static final Particle.DustOptions WHITE_PARTICLE = new Particle.DustOptions(Color.WHITE, 1.25f);
    private static final Particle.DustOptions LIGHT_BLUE_PARTICLE
            = new Particle.DustOptions(Color.fromRGB(0, 255, 255), 1.2f);

    private static final ConcurrentHashMap<Player, PlayerState> playerStates = new ConcurrentHashMap<>();

    @Override
    public void execute(Player player, Event genericEvent) {
        if (!playerStates.containsKey(player)) {
            playerStates.put(player, new PlayerState(player));
        }
    }

    @Override
    public boolean canActivate(Player player, Event genericEvent) {
        return !playerStates.containsKey(player);
    }

    @Override
    public void periodic() {
        playerStates.forEach((player, state) -> {
            if (player == null || !player.isOnline() || player.isDead()) {
                stop(player);
                return;
            }
            state.update(player);
        });
    }

    private void stop(Player player) {
        if (player == null) return;
        player.setInvulnerable(false);
        playerStates.remove(player);
    }

    private class PlayerState {

        private final long startTime;
        private int i = 0;

        public PlayerState(Player player) {
            this.startTime = System.currentTimeMillis();
            player.setFallDistance(0);
            performDash(player);
            player.setInvulnerable(true);
        }

        public void update(Player player) {
            double elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0;
            if (elapsedTime > VULNERABILITY_DURATION) {
                applySpeedBuff(player);
                finalAttack(player);
                stop(player);
            }
            if (i % 2 == 0) {
                spawnParticles(player);
                player.playSound(player, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.5f, 1);
                levitateEnemies(player);
            }
            i++;
        }

        private void performDash(Player player) {
            Vector direction = player.getLocation().getDirection()
                    .add(new Vector(0, 0.15, 0)).normalize();
            player.setVelocity(direction.multiply(DASH_RANGE));
        }

        private void applySpeedBuff(Player player) {
            StatSystem.getInstance().getStats(player).addModifier(
                    "bladeDanceSpeedBuff", CustomStat.SPEED, 0,
                    SPEED_BUFF, (int) SPEED_DURATION * 20);
        }

        private void finalAttack(Player player) {
            player.playSound(player, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.5f, 1);
            Location center = AbilityUtility.getEntityMiddle(player);
            for (Vector pos : AbilityUtility.
                    generatePointCircle(center.toVector(), FINAL_KNOCKBACK_RANGE, 12)) {
                player.getWorld().spawnParticle(
                        Particle.SWEEP_ATTACK, pos.toLocation(player.getWorld()), 1);
            }
        }

        private void spawnParticle(Location location, int count, double range,
                                   Particle particle, Particle.DustOptions data) {
            location.getWorld().spawnParticle(particle, location, count,
                    range, range, range, data);
        }

        private void spawnParticles(Player player) {
            Location location = AbilityUtility.getEntityMiddle(player);
            player.getWorld().spawnParticle(Particle.REDSTONE, location, 3,
                    PARTICLE_RANGE, PARTICLE_RANGE, PARTICLE_RANGE, RED_PARTICLE);

            spawnParticle(location, 2, PARTICLE_RANGE,
                    Particle.CLOUD, null);
            spawnParticle(location, 4, PARTICLE_RANGE * 1.35,
                    Particle.REDSTONE, WHITE_PARTICLE);
            spawnParticle(location, 1, PARTICLE_RANGE / 2,
                    Particle.REDSTONE, LIGHT_BLUE_PARTICLE);
            spawnParticle(location, 2, PARTICLE_RANGE * 1.15,
                    Particle.SWEEP_ATTACK, null);
        }

        private void levitateEnemies(Player player) {
            for (LivingEntity entity : AbilityUtility.entitiesInRadius(
                    AbilityUtility.getEntityMiddle(player), LEVITATION_RANGE)) {

                entity.addPotionEffect(new PotionEffect(
                        PotionEffectType.LEVITATION, 15,
                        3, true, false, false));

                entity.addPotionEffect(new PotionEffect(
                        PotionEffectType.SLOW_FALLING, 40,
                        4, true, false, false));

                StunEffectManager.addStunnedEntity(entity, 3);
            }
        }
    }
}