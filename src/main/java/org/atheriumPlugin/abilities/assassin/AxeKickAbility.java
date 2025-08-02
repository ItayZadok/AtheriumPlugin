package org.atheriumPlugin.abilities.assassin;

import org.atheriumPlugin.abilities.AbilityBase;
import org.atheriumPlugin.combat.CombatSystem;
import org.atheriumPlugin.stats.CustomStat;
import org.atheriumPlugin.stats.StatSystem;
import org.atheriumPlugin.utility.AbilityUtility;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

import java.util.List;

public class AxeKickAbility extends AbilityBase {

    private static final double RANGE = 6;
    private static final double KICKS = 5;
    private static final double SPREAD = 22.5;
    private static final double DEBUFF_DURATION = 3;

    private static final double HEIGHT_SPREAD = 0.05;
    private static final Particle.DustOptions color =
            new Particle.DustOptions(Color.GRAY, 1.75f);

    public void execute(Player player, Event genericEvent) {
        playSound(player);
        dealDamage(player);
        spawnParticles(player);
    }

    private void spawnParticles(Player player) {
        Vector start = AbilityUtility.getEntityMiddle(player).add(0, HEIGHT_SPREAD, 0).toVector();
        Vector direction = player.getLocation().getDirection().setY(HEIGHT_SPREAD * 0.75).normalize();
        World world = player.getWorld();

        for (int i = (int) (-KICKS / 2); i < KICKS / 2; i++) {
            Vector kickDirection = direction.clone().rotateAroundY(Math.toRadians(i * SPREAD));
            Vector end = start.clone().add(kickDirection.multiply(RANGE));

            world.spawnParticle(Particle.SWEEP_ATTACK, end.getMidpoint(start).toLocation(world),
                    1, 0, 0, 0);

            for (Vector pos : AbilityUtility.generateLine(start, end, 0.65)) {
                world.spawnParticle(Particle.REDSTONE, pos.toLocation(world), 1, 0, 0, 0, 0, color);
            }
        }
    }

    private void dealDamage(Player player) {
        Location location = AbilityUtility.getEntityMiddle(player).
                add(player.getLocation().getDirection().normalize().multiply(RANGE / 2));
        List<LivingEntity> entities = AbilityUtility.entitiesInRadius(location, RANGE / 2);
        double damage = StatSystem.getInstance().getStats(player).getStat(CustomStat.DAMAGE) * 0.5;

        for (LivingEntity entity : entities) {
            if (entity == null) continue;
            CombatSystem.dealDamage(entity, player, damage);
            StatSystem.getInstance().getStats(entity).addModifier("axeKickWeakness",
                    CustomStat.DEFENCE, -10, 0, (long) (20 * DEBUFF_DURATION));
            StatSystem.getInstance().getStats(entity).addModifier("axeKickSlowness",
                    CustomStat.SPEED, -10, 0, (long) (20 * DEBUFF_DURATION));
        }
    }

    private void playSound(Player player) {
        player.playSound(player, Sound.ENTITY_PLAYER_ATTACK_CRIT, 0.75F, 1.5F);
    }
}
