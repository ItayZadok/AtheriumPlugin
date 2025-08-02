package org.atheriumPlugin.abilities.knight;

import org.atheriumPlugin.AtheriumPlugin;
import org.atheriumPlugin.abilities.AbilityBase;
import org.atheriumPlugin.stats.CustomStat;
import org.atheriumPlugin.stats.StatSystem;
import org.atheriumPlugin.utility.AbilityUtility;
import org.atheriumPlugin.utility.GlowingEffectUtil;
import org.atheriumPlugin.utility.StunEffectManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ChainAbility extends AbilityBase {

    private static final ChatColor TARGET_OUTLINE_COLOR = ChatColor.DARK_GRAY;
    private static final int WEAKNESS_DURATION = 7;
    private static final double MIN_RADIUS = 2, MAX_RADIUS = 25, WEAKNESS_AMOUNT = 10,
            STUN_DURATION = 1, SPREAD_ANGLE = 65;

    @Override
    public void execute(Player player, Event genericEvent) {
        List<LivingEntity> enemies = selectTargets(player);
        if (enemies.isEmpty()) return;
        applyEffects(player, enemies);
        playSound(player);
        scheduleGlowRemoval(enemies);
    }

    @Override
    public boolean canActivate(Player player, Event genericEvent) {
        return AbilityUtility.getTargetEntity(player, MAX_RADIUS) != null;
    }

    private List<LivingEntity> selectTargets(Player player) {
        List<LivingEntity> enemies = new ArrayList<>();
        LivingEntity mainTarget = AbilityUtility.getTargetEntity(player, MAX_RADIUS);
        if (mainTarget != null) {
            enemies.add(mainTarget);
            List<LivingEntity> nearbyEnemies = AbilityUtility.entitiesInRadius(mainTarget.getLocation(), 4);
            for (int i = 0; i < nearbyEnemies.size() && enemies.size() < 3; i++) {
                enemies.add(nearbyEnemies.get(i));
            }
        }
        return enemies;
    }

    private void applyEffects(Player player, List<LivingEntity> enemies) {
        for (LivingEntity entity : enemies) {
            GlowingEffectUtil.setGlowing(entity, TARGET_OUTLINE_COLOR, true);

            StatSystem.getInstance().getStats(entity).addModifier(
                    "ChainWeakness", CustomStat.DEFENCE, -WEAKNESS_AMOUNT,
                    0, WEAKNESS_DURATION * 20);

            StunEffectManager.addStunnedEntity(entity, STUN_DURATION);
        }
        positionTargets(player, enemies);
    }

    private void scheduleGlowRemoval(List<LivingEntity> enemies) {
        Bukkit.getScheduler().runTaskLater(AtheriumPlugin.getInstance(), () -> {
            for (LivingEntity entity : enemies) {
                if (entity != null && !entity.isDead())
                    GlowingEffectUtil.setGlowing(entity, TARGET_OUTLINE_COLOR, false);
            }
        }, WEAKNESS_DURATION * 20);
    }

    private void positionTargets(Player player, List<LivingEntity> entities) {
        if (entities.isEmpty()) return;
        LivingEntity mainTarget = entities.get(0);

        Vector mainPosition = AbilityUtility.getEntityMiddle(player).toVector()
                .add(player.getLocation().getDirection().multiply(MIN_RADIUS))
                .setY(player.getLocation().getY() + 0.5);

        mainTarget.teleport(mainPosition.toLocation(player.getWorld()));
        mainTarget.setRotation(-player.getLocation().getYaw(), 0);

        if (entities.size() > 1) positionSideTarget(player, entities.get(1), mainPosition, SPREAD_ANGLE);
        if (entities.size() > 2) positionSideTarget(player, entities.get(2), mainPosition, -SPREAD_ANGLE);
    }

    private void positionSideTarget(Player player, LivingEntity target, Vector mainPosition, double angle) {
        Vector offset = player.getLocation().getDirection().rotateAroundY(Math.toRadians(angle))
                .multiply(MIN_RADIUS / 2);

        target.teleport(mainPosition.clone().subtract(offset).toLocation(player.getWorld()));
        target.setRotation(-player.getLocation().getYaw(), 0);
    }

    private void playSound(Player player) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 0.5F, 1.0F);
        player.playSound(player, Sound.ITEM_SHIELD_BLOCK, 0.5F, 1.0F);
        player.playSound(player, Sound.ITEM_SHIELD_BREAK, 0.5F, 0.5F);
    }
}
