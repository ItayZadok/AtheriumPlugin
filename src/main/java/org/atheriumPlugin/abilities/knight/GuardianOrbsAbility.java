package org.atheriumPlugin.abilities.knight;

import org.atheriumPlugin.AtheriumPlugin;
import org.atheriumPlugin.abilities.AbilityBase;
import org.atheriumPlugin.abilities.InputType;
import org.atheriumPlugin.items.ItemType;
import org.atheriumPlugin.stats.CustomStat;
import org.atheriumPlugin.stats.StatSystem;
import org.atheriumPlugin.utility.AbilityUtility;
import org.bukkit.*;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class GuardianOrbsAbility extends AbilityBase {

    private final static int ORB_COUNT = 3;
    private final static double ORB_RADIUS = 2;
    private final static double DURATION = 10; // in seconds
    private final static double SLOW_RADIUS = 2.5;
    private final static double SLOW_DECREASE = 5;
    private final static double ATTACK_SPEED_PERCENT_BUFF = 25;
    private final static float ORB_SIZE = 0.5F;

    private final static HashMap<Player, OrbsTask> playerTasks = new HashMap<>();

    @Override
    public void execute(Player player, Event genericEvent) {
        playerTasks.put(player, new OrbsTask(player));
    }

    @Override
    public void periodic() {
        playerTasks.entrySet().removeIf(entry -> {
            Player player = entry.getKey();
            if (player == null || !player.isValid()) {
                stopOrbs(player);
                return true;
            }
            entry.getValue().update();
            return false;
        });
    }

    @EventHandler
    public void onSwitchItem(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (playerTasks.get(player) == null) return;
        // axe because it checks right before they items is switched
        if (InputType.isHolding(player, EquipmentSlot.HAND, ItemType.AXE)) {
            stopOrbs(player);
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        OrbsTask orbsTaskTask = playerTasks.get(player);
        if (orbsTaskTask == null) return;

        orbsTaskTask.removeClosestOrb(event.getDamager().getLocation());
        StatSystem.getInstance().getStats(player)
                .addOneTimeModifier(CustomStat.HEALTH, 3, 0);
    }

    private static void stopOrbs(Player player) {
        playerTasks.get(player).removeAllOrbs();
        playerTasks.remove(player);
    }

    private static class OrbsTask {
        private final List<BlockDisplay> orbDisplays = new LinkedList<>();
        private final Player player;
        private double angle = 0;
        private final long startTime = System.currentTimeMillis();

        public OrbsTask(Player player) {
            this.player = player;
            spawnOrbs(player);
            player.playSound(player, Sound.BLOCK_BEACON_POWER_SELECT, 0.5F, 1F);
        }

        public void update() {
            if (orbDisplays.isEmpty() || getDuration() >= DURATION) {
                removeAllOrbs();
                stopOrbs(player);
                return;
            }
            rotateOrbs();
            for (BlockDisplay blockDisplay : orbDisplays) {
                if (blockDisplay != null) applySlowEffect(blockDisplay);
            }
        }

        private long getDuration() {
            return (System.currentTimeMillis() - startTime) / 1000;
        }

        private void spawnOrbs(Player player) {
            Location playerLocation = player.getLocation();
            for (int i = 0; i < ORB_COUNT; i++) {
                BlockDisplay orbDisplay = player.getWorld().spawn(playerLocation, BlockDisplay.class);
                orbDisplay.setBlock(Material.OCHRE_FROGLIGHT.createBlockData());
                Transformation transformation = new Transformation(
                        new Vector3f(0, 0, 0),
                        new Quaternionf(),
                        new Vector3f(ORB_SIZE, ORB_SIZE, ORB_SIZE),
                        new Quaternionf()
                );
                orbDisplay.setTeleportDuration(2);
                orbDisplay.setTransformation(transformation);
                orbDisplays.add(orbDisplay);
            }
        }

        public void removeClosestOrb(Location hitDirection) {
            BlockDisplay closestOrb = null;
            double closestDistance = Double.MAX_VALUE;

            for (BlockDisplay orb : orbDisplays) {
                double distance = orb.getLocation().distance(hitDirection);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestOrb = orb;
                }
            }

            if (closestOrb != null) {
                removeOrb(closestOrb);
                buffPlayer();
            }
        }

        private void rotateOrbs() {
            Location playerLocation = player.getLocation().setDirection(new Vector(0, 0, 0));
            angle += Math.PI / 16;

            for (int i = 0; i < orbDisplays.size(); i++) {
                BlockDisplay orb = orbDisplays.get(i);
                if (orb == null) return;

                double x = ORB_RADIUS * Math.cos(angle + (i * 2 * Math.PI / ORB_COUNT));
                double z = ORB_RADIUS * Math.sin(angle + (i * 2 * Math.PI / ORB_COUNT));
                Vector offset = new Vector(x, 1, z);
                orb.teleport(playerLocation.clone().add(offset));
                orb.getWorld().spawnParticle(Particle.SPELL_INSTANT, orb.getLocation().subtract(0, 0.5, 0),
                        1, 0.2, 0.2, 0.2, 0);
            }
        }

        private void removeAllOrbs() {
            List<BlockDisplay> toRemove = new LinkedList<>(orbDisplays);
            for (BlockDisplay blockDisplay : toRemove) {
                if (blockDisplay != null) {
                    removeOrb(blockDisplay); // Remove the orb
                }
            }
        }

        private void applySlowEffect(BlockDisplay blockDisplay) {
            List<LivingEntity> entities = AbilityUtility.entitiesInRadius(blockDisplay.getLocation(), SLOW_RADIUS);
            for (LivingEntity entity : entities) {
                StatSystem.getInstance().getStats(entity).addModifier("guardianOrbSlowness", CustomStat.SPEED,
                        -SLOW_DECREASE, 0, 20);
            }
        }

        private void buffPlayer() {
            player.stopSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);

            Bukkit.getScheduler().runTaskLater(AtheriumPlugin.getInstance(), () ->
                    StatSystem.getInstance().getStats(player).addModifier("guardianOrbAttackSpeed",
                            CustomStat.ATTACK_SPEED, 0, ATTACK_SPEED_PERCENT_BUFF, 100), 1);
        }

        private void removeOrb(BlockDisplay blockDisplay) {
            blockDisplay.getWorld().playEffect(blockDisplay.getLocation().subtract(new Vector(0, 1, 0)),
                    Effect.STEP_SOUND, Material.OCHRE_FROGLIGHT);
            blockDisplay.getWorld().spawnParticle(Particle.END_ROD,
                    blockDisplay.getLocation(), 10, 0.5, 0.5, 0.5, 0.01);
            player.getWorld().playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 0.3f, 1.25f);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT, 0.3f, 0.25f);

            orbDisplays.remove(blockDisplay);
            blockDisplay.remove();
        }
    }

    @Override
    public void onDisable() {
        for (OrbsTask task : playerTasks.values()) {
            task.removeAllOrbs();
        }
    }
}