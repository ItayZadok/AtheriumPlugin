package org.atheriumPlugin.abilities.assassin;

import org.atheriumPlugin.abilities.AbilityBase;
import org.atheriumPlugin.abilities.AbilitySystem;
import org.atheriumPlugin.combat.CombatSystem;
import org.atheriumPlugin.player.PlayerAbility;
import org.atheriumPlugin.player.PlayerAbilityManager;
import org.atheriumPlugin.player.PlayerSystem;
import org.atheriumPlugin.stats.CustomStat;
import org.atheriumPlugin.stats.StatSystem;
import org.atheriumPlugin.utility.AbilityUtility;
import org.atheriumPlugin.utility.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;

public class ExecutionFlow extends AbilityBase {

    private static final double REFRESH_AMOUNT = 4; // seconds
    private static final double HEAL_AMOUNT = 2;
    private static final double PARTICLE_RANGE = 1;
    private static final double DAMAGE_PERCENT = 0.4;
    private static final double HIT_RADIUS = 3;

    @Override
    public void execute(Player player, Event genericEvent) {
        if (!(genericEvent instanceof EntityDeathEvent event)) return;

        healPlayer(player);
        damageNearbyEnemies(player);
        refreshAbilities(player);
        playSounds(player);
        spawnParticles(event.getEntity());
    }

    private void spawnParticles(LivingEntity target) {
        target.getWorld().spawnParticle(Particle.HEART, target.getEyeLocation(), 3,
                PARTICLE_RANGE, PARTICLE_RANGE, PARTICLE_RANGE);
    }

    private void playSounds(Player player) {
        player.playSound(player, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 0.75f, 1f);
        player.playSound(player, Sound.BLOCK_AMETHYST_CLUSTER_STEP, 3f, 1f);
    }

    private void refreshAbilities(Player player) {
        PlayerAbilityManager playerAbilityManager = PlayerSystem.getInstance().getAbilityManager(player);
        for (PlayerAbility ability : playerAbilityManager.getUnlockedAbilities()) {
            ability.reduceCooldown(REFRESH_AMOUNT);
        }
    }

    private void damageNearbyEnemies(Player player) {
        double damage = 24 + StatSystem.getStat(player, CustomStat.DAMAGE) * DAMAGE_PERCENT;

        for (LivingEntity entity : AbilityUtility.entitiesInRadius(player.getLocation(), HIT_RADIUS)) {
            CombatSystem.dealDamage(entity, player, damage);
        }
    }

    private void healPlayer(Player player) {
        StatSystem.getInstance().getStats(player).
                addOneTimeModifier(CustomStat.HEALTH, HEAL_AMOUNT, 0);
    }
}
