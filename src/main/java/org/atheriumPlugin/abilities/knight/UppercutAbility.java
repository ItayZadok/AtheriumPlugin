package org.atheriumPlugin.abilities.knight;

import org.atheriumPlugin.abilities.AbilityBase;
import org.atheriumPlugin.stats.CustomStat;
import org.atheriumPlugin.stats.StatSystem;
import org.atheriumPlugin.utility.AbilityUtility;
import org.atheriumPlugin.utility.GlowingEffectUtil;
import org.atheriumPlugin.utility.StunEffectManager;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Objects;

public class UppercutAbility extends AbilityBase {

    private static final double RANGE = 5;
    private static final double PARTICLE_RADIUS = 0.5;
    private static final double PARTICLE_RATE = 5;
    private static final int DURATION = 60;
    private static final int HIT_RANGE = 5;

    private static final HashMap<Player, PlayerState> playerStates = new HashMap<>();

    @Override
    public void execute(Player player, Event genericEvent) {
        LivingEntity target = AbilityUtility.getTargetEntity(player, RANGE);
        if (target == null) return;
        playerStates.put(player, new PlayerState(player, target));
    }

    @Override
    public void periodic() {
        playerStates.entrySet().removeIf(entry -> {
            Player player = entry.getKey();
            if (player == null || player.isDead() || !player.isOnline()) {
                entry.getValue().stopUppercut();
                return true;
            }
            entry.getValue().update();
            return false;
        });
    }

    @Override
    public boolean canActivate(Player player, Event genericEvent) {
        return AbilityUtility.getTargetEntity(player, RANGE) != null &&
                !playerStates.containsKey(player);
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!(!player.isOnGround() && player.getVelocity().getY() < 0)) return;
        PlayerState playerState = playerStates.get(player);
        if (playerState == null) return;
        if (!playerState.target.equals(event.getEntity())) return;
        playerState.handleUppercutHit();
    }

    public static class PlayerState {

        private double remainingDuration = DURATION;
        private double angle = 0;
        private final Player player;
        private final LivingEntity target;

        public PlayerState(Player player, LivingEntity target) {
            this.player = player;
            this.target = target;
            playGlowingEffect();
            performUppercut();
            applyStunEffect();
        }

        private void spawnCircularTrailParticle(LivingEntity target, double angle) {
            Vector center = target.getLocation().toVector().add(new Vector(0, 0.25, 0));
            World world = target.getWorld();
            double x = PARTICLE_RADIUS * Math.cos(angle);
            double z = PARTICLE_RADIUS * Math.sin(angle);
            world.spawnParticle(Particle.END_ROD, center.add(new Vector(x, 0, z)).toLocation(world),
                    2, 0, 0, 0, 0);
        }

        private void spawnCircleParticles(LivingEntity target) {
            Vector center = target.getLocation().toVector().subtract(new Vector(0, 0.25, 0));
            World world = target.getWorld();
            for (Vector pos : AbilityUtility.generatePointCircle(center, PARTICLE_RADIUS, 20)) {
                world.spawnParticle(Particle.CRIT, pos.toLocation(world), 1, 0, 0, 0, 0.01);
            }
        }

        private void handleUppercutHit() {
            playHitSounds();
            spawnHitParticles();
            dealUppercutWeakness();
            playSound();
            player.setFallDistance(-500); // remove fall damage only if hit the target
            stopUppercut();
        }

        private void update() {
            if (target == null || target.isDead() || (target.isOnGround() && target.getVelocity().getY() < 0)
                    || remainingDuration < 0) {
                stopUppercut();
                return;
            }
            if (remainingDuration % PARTICLE_RATE == 0) {
                spawnCircleParticles(target);
            }
            angle += Math.PI / 5;
            spawnCircularTrailParticle(target, angle);
            remainingDuration--;
        }

        private void playSound() {
            Location location = target.getLocation();
            Objects.requireNonNull(location.getWorld()).playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
        }

        private void spawnHitParticles() {
            Particle.DustOptions particle = new Particle.DustOptions(Color.RED, 1f);
            Vector top = target.getLocation().toVector().add(new Vector(0, 1, 0));
            Vector bottom = target.getLocation().toVector().subtract(new Vector(0, 2, 0));
            World world = target.getWorld();
            for (Vector pos : AbilityUtility.generateLine(top, bottom, 0.05)) {
                world.spawnParticle(Particle.REDSTONE, pos.toLocation(world), 1, particle);
            }
            world.spawnParticle(Particle.EXPLOSION_LARGE, AbilityUtility.getEntityMiddle(target),
                    1, 0, 0, 0);
        }

        private void playHitSounds() {
            player.playSound(player, Sound.ENTITY_WITHER_BREAK_BLOCK, 0.1F, 0.5F);
            player.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.1F, 0.5F);
            player.playSound(player, Sound.ENTITY_GENERIC_EXPLODE, 0.2F, 0.5F);
        }

        private void dealUppercutWeakness() {
            Location location = target.getLocation();
            for (LivingEntity entity : AbilityUtility.entitiesInRadius(location, HIT_RANGE)) {
                StatSystem.getInstance().getStats(entity).addModifier("uppercutWeakness",
                        CustomStat.DEFENCE, -7.5, 0, 20 * 7);
                StunEffectManager.addStunnedEntity(entity, 0.5);
            }
        }

        private void cleanUpUppercut() {
            GlowingEffectUtil.setGlowing(target, ChatColor.GOLD, false);
        }

        private void performUppercut() {
            Vector up = player.getLocation().getDirection().setY(0);
            player.setVelocity(up.clone().multiply(-0.1).add(new Vector(0, 0.75f, 0)));
            target.setVelocity(up.clone().multiply(-0.1).add(new Vector(0, 0.75f, 0)));
            player.playSound(player, Sound.ITEM_TOTEM_USE, 0.25F, 0.5F);
            AbilityUtility.resetMobTarget(target);
        }

        private void playGlowingEffect() {
            GlowingEffectUtil.setGlowing(target, ChatColor.GOLD, true);
        }

        private void applyStunEffect() {
            StatSystem.getInstance().getStats(target).addModifier("uppercutWeakness",
                    CustomStat.DEFENCE, -10, 0, DURATION);
            target.setNoActionTicks(30);
        }

        private void stopUppercut() {
            cleanUpUppercut();
            playerStates.remove(player);
        }
    }
}