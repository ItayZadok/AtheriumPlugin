package org.atheriumPlugin.abilities.assassin;

import org.atheriumPlugin.abilities.AbilityBase;
import org.atheriumPlugin.abilities.AbilityType;
import org.atheriumPlugin.player.PlayerSystem;
import org.atheriumPlugin.stats.CustomStat;
import org.atheriumPlugin.stats.StatSystem;
import org.atheriumPlugin.utility.AbilityUtility;
import org.atheriumPlugin.utility.GlowingEffectUtil;
import org.atheriumPlugin.utility.Logger;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

import java.util.concurrent.ConcurrentHashMap;

public class ComboStarAbility extends AbilityBase {

    private static final double MAX_DISTANCE = 15;
    private static final double DAMAGE_BUFF = 40;
    private static final int DAMAGE_DURATION = 3; // in seconds
    private static final double RECHARGE_DURATION = 1.35; // in seconds
    private static final double STOP_DISTANCE = 3;

    private static final ConcurrentHashMap<Player, PlayerState> playerStates = new ConcurrentHashMap<>();

    @Override
    public void execute(Player player, Event genericEvent) {
        LivingEntity target = AbilityUtility.getTargetEntity(player, MAX_DISTANCE);
        if (target == null) return;

        GlowingEffectUtil.setGlowing(target, ChatColor.DARK_GRAY, true);
        dashToTarget(player, target);
        player.playSound(player, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1F, 0.8F);
        player.playSound(player, Sound.ITEM_TRIDENT_RIPTIDE_1, 1F, 0.9F);

        StatSystem.getInstance().getStats(player)
                .addModifier("dashStrength", CustomStat.DAMAGE,
                        0, DAMAGE_BUFF, DAMAGE_DURATION * 20);
    }

    @Override
    public boolean canActivate(Player player, Event genericEvent) {
        return !playerStates.containsKey(player) &&
                AbilityUtility.getTargetEntity(player, MAX_DISTANCE) != null;
    }

    /**
     * Teleports a player close to the target entity, just behind them.
     * Avoids teleporting into solid blocks and clips slightly above ground.
     */
    private void dashToTarget(Player player, LivingEntity target) {
        if (!target.isValid()) return;

        Location from = player.getLocation();
        Location to = target.getLocation().clone();
        Vector direction = to.toVector().subtract(from.toVector()).normalize();

        Location dashLocation = to.clone().subtract(direction.multiply(STOP_DISTANCE));
        dashLocation.setY(to.getY() + 1); // lift slightly to avoid ground clipping

        if (dashLocation.getBlock().getType().isSolid()) return;

        Vector afterTeleportDirection = target.getEyeLocation().clone().subtract(dashLocation).
                subtract(new Vector(0, 1.25, 0)).toVector().normalize();
        player.teleport(dashLocation.setDirection(afterTeleportDirection));

        player.teleport(dashLocation);
        summonParticles(player);
        playerStates.put(player, new PlayerState(player, target));
    }

    private void summonParticles(Player player) {
        World world = player.getWorld();
        Location location = AbilityUtility.getEntityMiddle(player);
        world.spawnParticle(Particle.CRIT_MAGIC, location, 20, 1, 1, 1, 1);
        world.spawnParticle(Particle.ASH, location, 20, 1, 1, 1, 1);
        world.spawnParticle(Particle.REDSTONE, location, 20, 1, 1, 1, 1,
                new Particle.DustOptions(Color.BLACK, 1));
        world.spawnParticle(Particle.SPELL_WITCH, location, 10, 0.75, 0.75, 0.75, 1);
    }

    @Override
    public void periodic() {
        playerStates.forEach((player, state) -> {
            if (player == null || player.isDead() || !player.isOnline()) {
                stop(player);
                return;
            }
            state.update();
        });
    }

    private static void stop(Player player) {
        playerStates.remove(player);
    }

    private static class PlayerState {

        private final Player player;
        private final LivingEntity target;
        private final long startTime;

        public PlayerState(Player player, LivingEntity target) {
            this.player = player;
            this.target = target;
            this.startTime = System.currentTimeMillis();
        }

        public void update() {
            double elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0;
            if (elapsedTime > RECHARGE_DURATION || target.isDead()) {
                stop(player);
                if (target.isDead()) {
                    player.playSound(player, Sound.ENTITY_GHAST_DEATH, 0.2f, 1);
                    player.playSound(player, Sound.ITEM_SHIELD_BREAK, 0.5f, 1);
                    PlayerSystem.getInstance().getAbilityManager(player)
                            .getPlayerAbility(AbilityType.COMBO_STAR).setCooldownZero();
                } else {
                    GlowingEffectUtil.setGlowing(target, ChatColor.DARK_GRAY, false);
                }
            }
        }
    }
}
