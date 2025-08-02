package org.atheriumPlugin.abilities.assassin;

import org.atheriumPlugin.AtheriumPlugin;
import org.atheriumPlugin.abilities.AbilityBase;
import org.atheriumPlugin.stats.CustomStat;
import org.atheriumPlugin.stats.StatSystem;
import org.atheriumPlugin.utility.AbilityUtility;
import org.atheriumPlugin.utility.StunEffectManager;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitTask;

public class EscapeArtistAbility extends AbilityBase {

    private static final long REGEN_DURATION = 3*20;
    private static final int REGEN_RATE = 7;
    private static final double INVIS_DURATION = 3;
    private static final double SMOKE_BOMB_RANGE = 3.5;
    private static final int PARTICLE_AMOUNT = 125;
    private static final double TRIGGER_HP_PERCENTAGE = 0.2;
    private static final double STUN_DURATION = 1.5;
    private static final double SPEED_DURATION = 2.5;
    private static final Particle.DustOptions color = new Particle.DustOptions(Color.GRAY, 1.75f);

    public void execute(Player player, Event genericEvent) {
        playSound(player);
        spawnParticles(player);

        StatSystem.getInstance().getStats(player)
                .healInterval(1, REGEN_DURATION, REGEN_RATE);

        StatSystem.getInstance().getStats(player).addModifier("escapeArtistSpeed", CustomStat.SPEED,
                10, 0, (long) SPEED_DURATION * 20);
        stunMobs(AbilityUtility.getEntityMiddle(player));

        player.setInvulnerable(true);
        player.setInvisible(true);
        Bukkit.getScheduler().runTaskLater(AtheriumPlugin.getInstance(),
                () -> {
                    player.setInvulnerable(false);
                    player.setInvisible(false);
                },
                (long) INVIS_DURATION * 20);
    }

    @Override
    public boolean canActivate(Player player, Event genericEvent) {
        double health = StatSystem.getInstance().getStats(player).getStat(CustomStat.HEALTH);
        double maxHealth = StatSystem.getInstance().getStats(player).getStat(CustomStat.MAX_HEALTH);
        return health <= maxHealth * TRIGGER_HP_PERCENTAGE;
    }

    private void stunMobs(Location location) {
        for (LivingEntity entity : AbilityUtility.entitiesInRadius(location, SMOKE_BOMB_RANGE)) {
            StunEffectManager.addStunnedEntity(entity, STUN_DURATION);
        }
    }

    private void spawnParticles(Player player) {
        Location center = AbilityUtility.getEntityMiddle(player);
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(AtheriumPlugin.getInstance(), () -> {
            player.getWorld().spawnParticle(Particle.REDSTONE, center, PARTICLE_AMOUNT,
                    SMOKE_BOMB_RANGE, SMOKE_BOMB_RANGE, SMOKE_BOMB_RANGE, color);
        }, 0, 20); // 0 tick delay, repeat every 20 ticks (1 second)

        Bukkit.getScheduler().runTaskLater(AtheriumPlugin.getInstance(), task::cancel, (long) (INVIS_DURATION * 20));
    }

    private void playSound(Player player) {
        player.playSound(player, Sound.ENTITY_PLAYER_ATTACK_CRIT, 0.75F, 1.5F);
    }
}
