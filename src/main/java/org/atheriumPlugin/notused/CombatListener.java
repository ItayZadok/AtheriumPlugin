//package org.atheriumPlugin.stats;
//
//import org.atheriumPlugin.items.ItemType;
//import org.atheriumPlugin.player.PlayerItemManager;
//import org.atheriumPlugin.player.PlayerManager;
//import org.atheriumPlugin.utility.AbilityUtility;
//import org.atheriumPlugin.utility.StunEffectManager;
//import org.bukkit.Color;
//import org.bukkit.Location;
//import org.bukkit.Particle;
//import org.bukkit.World;
//import org.bukkit.entity.Arrow;
//import org.bukkit.entity.Entity;
//import org.bukkit.entity.LivingEntity;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.entity.EntityDamageByEntityEvent;
//import org.bukkit.inventory.EquipmentSlot;
//import org.bukkit.util.Vector;
//
//import java.util.List;
//
//public class CombatListener implements Listener {
//
//    private static final double SMALL_HIT_ROTATION = 0.5;
//    private static final double SMALL_HIT_RANGE = 0.5;
//    private static final double SMALL_HIT_PARTICLE_DENSITY = 0.05;
//
//    @EventHandler
//    public void onHit(EntityDamageByEntityEvent event) {
//        if (!(event.getEntity() instanceof LivingEntity damaged)) return;
//        event.setDamage(0);
//        if (event.getDamager() instanceof Player player) {
//            handlePlayerAttack(player, damaged);
//            // particles
//            if (AbilityUtility.isCritical(player))
//                generateCriticalHitParticles(player, event.getEntity());
//            else if (player.getAttackCooldown() == 1)
//                generateNormalHitParticles(player, event.getEntity());
//            else
//                generateSmallHitParticles(event.getEntity());
//
//            return;
//        }
//        if (event.getDamager() instanceof LivingEntity attacker && !(attacker instanceof Player)) {
//            handleEnemyAttack(attacker, damaged);
//            return;
//        }
//        if (event.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player player) {
//            handlePlayerArrowAttack(player, damaged, arrow);
//            return;
//        }
//        if (event.getDamager() instanceof Arrow arrow && damaged instanceof Player player
//                && arrow.getShooter() instanceof LivingEntity entity && !(entity instanceof Player)) {
//            handleEnemyArrowAttack(entity, player, arrow);
//        }
//    }
//
//    private void handlePlayerAttack(Player player, LivingEntity damaged) {
//        double damage = StatEntity.getPlayerStats(player).getStat(CustomStat.DAMAGE);
//        if (AbilityUtility.isCritical(player)) {
//            damage *= 1.4;
//        } else if (player.getAttackCooldown() < 1) {
//            damage *= 0.25; // non-charged attack
//        }
//        PlayerItemManager playerItemManager = PlayerManager.getProfile(player).getItemManager();
//        if (playerItemManager.getItem(EquipmentSlot.HAND) != null &&
//                playerItemManager.getItem(EquipmentSlot.HAND).itemType().equals(ItemType.BOW)) {
//            damage = 1;
//        }
//        StatEntity.getMobStats(damaged).dealDamage(damage, player);
//    }
//
//    private void handleEnemyAttack(LivingEntity attacker, LivingEntity damaged) {
//        double damage = StatEntity.getMobStats(attacker).getStat(CustomStat.DAMAGE);
//        if (StunEffectManager.isStunnedEntity(damaged)) return;
//        if (damaged instanceof Player player) {
//            if (AbilityUtility.isChargingShield(player)) return;
//            StatEntity.getPlayerStats(player).dealDamage(damage, attacker);
//        } else {
//            StatEntity.getMobStats(damaged).dealDamage(damage, attacker);
//        }
//    }
//
//    private void handlePlayerArrowAttack(Player player, LivingEntity damaged, Arrow arrow) {
//        double bowDamage = StatEntity.getPlayerStats(player).getStat(CustomStat.DAMAGE);
//        if (arrow.getLocation().distance(damaged.getEyeLocation()) < 0.75) bowDamage *= 1.5; // headshot
//
//        float velocity = (float) arrow.getVelocity().length();
//        int chargeLevel = (int) Math.ceil(Math.min(velocity, 2.147483647E9));
//        StatEntity.getMobStats(damaged).dealDamage(bowDamage / 3 * chargeLevel,
//                player.getLocation().toVector(), 0.5, player);
//    }
//
//    private void handleEnemyArrowAttack(LivingEntity attacker, Player damaged, Arrow arrow) {
//        if (AbilityUtility.isChargingShield(damaged)) return;
//        double bowDamage = StatEntity.getMobStats(attacker).getStat(CustomStat.DAMAGE);
//        double velocity = arrow.getVelocity().length();
//        int chargeLevel = (int) Math.ceil(Math.min(velocity, 2.147483647E9));
//        StatEntity.getMobStats(damaged).dealDamage(bowDamage / 3 * chargeLevel,
//                arrow.getLocation().toVector(), 0.5, attacker);
//    }
//
//    private void generateSmallHitParticles(Entity entity) {
//        if (!(entity instanceof LivingEntity target)) return;
//
//        World world = target.getWorld();
//        Vector offset = new Vector(-SMALL_HIT_ROTATION + Math.random() * SMALL_HIT_ROTATION * 2,
//                SMALL_HIT_RANGE, -SMALL_HIT_ROTATION + Math.random() * SMALL_HIT_ROTATION * 2);
//        Vector middle = AbilityUtility.getEntityMiddle(target).toVector();
//        Vector start = middle.clone().add(offset);
//        Vector end = middle.clone().subtract(offset);
//
//        Particle.DustOptions dust = new Particle.DustOptions(
//                Color.fromRGB(255, 255, 255), 1F);
//        for (Vector vector : AbilityUtility.generateLine(start, end, SMALL_HIT_PARTICLE_DENSITY)) {
//            Location pos = vector.toLocation(world);
//            world.spawnParticle(Particle.REDSTONE,
//                    pos.getX(), pos.getY(), pos.getZ(), 1, 0, 0, 0, dust);
//        }
//    }
//
//    private void generateCriticalHitParticles(Player player, Entity entity) {
//        if (!(entity instanceof LivingEntity target)) return;
//
//        World world = player.getWorld();
//        Vector start = target.getLocation().toVector().add(new Vector(0, 0.25, 0));
//        Vector end = target.getEyeLocation().toVector().add(new Vector(0, 0.75, 0));
//        Vector direction = player.getLocation().getDirection();
//        start.subtract(new Vector(direction.getX() * 0.75, 0, direction.getZ() * 0.75));
//
//        Vector curveControlPoint = AbilityUtility.
//                findCurveControlPoint(start, end, direction, true);
//
//        List<Vector> points = AbilityUtility.
//                generateCurve(start, end, curveControlPoint, 0.1);
//        for (int i = 0; i < points.size(); i++) {
//            Location pos = points.get(i).toLocation(world);
//            double particleSize = 0.5 + (double) i / points.size();
//            Particle.DustOptions dust = new Particle.DustOptions(
//                    Color.fromRGB(204, 0, 102), (float) particleSize);
//            world.spawnParticle(Particle.REDSTONE, pos.getX(), pos.getY(), pos.getZ(),
//                    1, 0, 0, 0, dust);
//        }
//    }
//
//    private void generateNormalHitParticles(Player player, Entity entity) {
//        if (!(entity instanceof LivingEntity target)) return;
//
//        World world = player.getWorld();
//        Vector direction = player.getLocation().getDirection().clone();
//
//        if ((int) (Math.random() * 2) == 0) direction.rotateAroundY(90).normalize();
//        else direction.rotateAroundY(-90).normalize();
//
//        double x = -direction.clone().getX();
//        double z = -direction.clone().getZ();
//        double yOffset = -0.5 + Math.random() * 1.5;
//
//        Vector targetMid = AbilityUtility.getEntityMiddle(target).toVector();
//        targetMid.add(new Vector(0,
//                Math.sin(Math.PI / 2 * player.getLocation().getDirection().getY()), 0));
//        targetMid.add(new Vector(0, player.getLocation().
//                toVector().subtract(target.getLocation().toVector()).normalize().getY() * 2, 0));
//
//        double distance = 1.25 * target.getHeight() / 2 * (1 + Math.random() * 0.2);
//        Vector start = targetMid.clone().add(new Vector(-distance * x, 0, -distance * z));
//        Vector end = targetMid.clone().add(new Vector(distance * x, 0, distance * z));
//
//        start.add(new Vector(0, yOffset, 0));
//        end.add(new Vector(0, -yOffset, 0));
//
//        Vector curveControlPoint = AbilityUtility.
//                findCurveControlPoint(start, end, direction, true);
//        List<Vector> points = AbilityUtility.
//                generateCurve(start, end, curveControlPoint, 0.05);
//
//        int mid = points.size() / 2;
//        double minValue = 0.5, maxValue = 1;
//
//        for (int i = 0; i < points.size(); i++) {
//            Location pos = points.get(i).toLocation(world);
//            double normalizedDistance = Math.abs(i - mid) / (double) mid;
//            double particleSize = (1.2 - normalizedDistance) * (maxValue - minValue) + minValue;
//
//            Particle.DustOptions dust = new Particle.DustOptions(
//                    Color.fromRGB(255, 255, 255), (float) particleSize);
//            world.spawnParticle(Particle.REDSTONE, pos, 1, dust);
//        }
//    }
//}