//package org.atheriumPlugin.stats;
//
//import org.atheriumPlugin.AtheriumPlugin;
//import org.atheriumPlugin.utility.AbilityUtility;
//import org.bukkit.Bukkit;
//import org.bukkit.World;
//import org.bukkit.entity.Display;
//import org.bukkit.entity.LivingEntity;
//import org.bukkit.entity.Player;
//import org.bukkit.entity.TextDisplay;
//import org.bukkit.util.Vector;
//
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class StatEntity {
//
//    private static final ConcurrentHashMap<LivingEntity, StatEntity> statsEntityMap = new ConcurrentHashMap<>();
//    private static final LinkedList<TextDisplay> damageIndicators = new LinkedList<>();
//    private final static ConcurrentHashMap<LivingEntity, UUID> killedMobs = new ConcurrentHashMap<>();
//
//    private final LivingEntity entity;
//    private final ConcurrentHashMap<CustomStat, Double> baseStats = new ConcurrentHashMap<>();
//    private final ConcurrentHashMap<CustomStat, Double> notModifiedFlat = new ConcurrentHashMap<>();
//    private final ConcurrentHashMap<CustomStat, Double> notModifiedPercent = new ConcurrentHashMap<>();
//    private final ConcurrentHashMap<CustomStat, HashMap<String, Double>> flatModifiers = new ConcurrentHashMap<>();
//    private final ConcurrentHashMap<CustomStat, HashMap<String, Double>> percentModifiers = new ConcurrentHashMap<>();
//    private final ConcurrentHashMap<String, Long> timedModifiers = new ConcurrentHashMap<>();
//    private final ConcurrentHashMap<String, Integer> modifierRates = new ConcurrentHashMap<>();
//
//    private static long tick = 0;
//
//    private StatEntity(LivingEntity entity) {
//        this.entity = entity;
//        initializeDefaultStats();
//    }
//
//    private void initializeDefaultStats() {
//        setBaseStat(CustomStat.MAX_HEALTH, 20);
//        setBaseStat(CustomStat.DAMAGE, 1);
//        setBaseStat(CustomStat.DEFENCE, 0);
//        setBaseStat(CustomStat.SPEED, 20);
//        setBaseStat(CustomStat.ATTACK_SPEED, 10);
//        addOneTimeModifier(CustomStat.HEALTH, getBaseStat(CustomStat.MAX_HEALTH), 0);
//    }
//
//    public static StatEntity getMobStats(LivingEntity entity) {
//        return statsEntityMap.computeIfAbsent(entity, StatEntity::new);
//    }
//
//    public static StatEntity getPlayerStats(Player player) {
//        return statsEntityMap.computeIfAbsent(player, StatEntity::new);
//    }
//
//    public void addModifier(String key, CustomStat stat, double flatValue, double percentValue,
//                            long durationTicks) {
//        addModifier(key, stat, flatValue, percentValue, durationTicks, 1);
//    }
//
//    public void addModifier(String key, CustomStat stat, double flatValue, double percentValue,
//                            long durationTicks, int rateTicks) {
//        if (flatValue != 0) {
//            flatModifiers.computeIfAbsent(stat, k -> new HashMap<>()).put(key, flatValue);
//        }
//        if (percentValue != 0) {
//            percentModifiers.computeIfAbsent(stat, k -> new HashMap<>()).put(key, percentValue);
//        }
//        if (durationTicks > 0) {
//            timedModifiers.put(key + ":" + stat, System.currentTimeMillis() / 1000 + durationTicks / 20);
//        }
//        if (rateTicks > 0) {
//            modifierRates.put(key + ":" + stat, rateTicks);
//        }
//    }
//
//    public void addOneTimeModifier(CustomStat stat, double flatValue, double percentValue) {
//        addModifier(UUID.randomUUID().toString(), stat, flatValue, percentValue, 1, 1);
//    }
//
//    public void removeModifier(String key, CustomStat stat) {
//        flatModifiers.getOrDefault(stat, new HashMap<>()).remove(key);
//        percentModifiers.getOrDefault(stat, new HashMap<>()).remove(key);
//        timedModifiers.remove(key + ":" + stat);
//        modifierRates.remove(key + ":" + stat);
//    }
//
//    public double getStat(CustomStat stat) {
//        double flatTotal = 0;
//        double percentTotal = 0;
//        Map<String, Double> flatModifiersForStat = flatModifiers.getOrDefault(stat, new HashMap<>());
//        for (Map.Entry<String, Double> entry : new HashSet<>(flatModifiersForStat.entrySet())) { // Create a copy to safely iterate
//            String key = entry.getKey();
//            int rate = modifierRates.getOrDefault(key + ":" + stat, 1);
//            if (tick % rate == 0) {
//                flatTotal += entry.getValue();
//            }
//        }
//        Map<String, Double> percentModifiersForStat = percentModifiers.getOrDefault(stat, new HashMap<>());
//        for (Map.Entry<String, Double> entry : new HashSet<>(percentModifiersForStat.entrySet())) { // Create a copy to safely iterate
//            String key = entry.getKey();
//            int rate = modifierRates.getOrDefault(key + ":" + stat, 1);
//            if (tick % rate == 0) {
//                percentTotal += entry.getValue();
//            }
//        }
//        percentTotal += getNotModifiedPercentStat(stat);
//        return (getBaseStat(stat) + getNotModifiedFlatStat(stat) + flatTotal) * (1 + percentTotal / 100);
//    }
//
//    public double getBaseStat(CustomStat stat) {
//        return stat == CustomStat.HEALTH ? entity.getHealth() : baseStats.getOrDefault(stat, 0.0);
//    }
//
//    public void setBaseStat(CustomStat stat, double value) {
//        baseStats.put(stat, value);
//    }
//
//    public double getNotModifiedFlatStat(CustomStat stat) {
//        return notModifiedFlat.getOrDefault(stat, 0.0);
//    }
//
//    public double getNotModifiedPercentStat(CustomStat stat) {
//        return notModifiedPercent.getOrDefault(stat, 0.0);
//    }
//
//    public void setNotModifiedFlatStat(CustomStat stat, double value) {
//        notModifiedFlat.put(stat, value);
//    }
//
//    public void setNotModifiedPercent(CustomStat stat, double value) {
//        notModifiedPercent.put(stat, value);
//    }
//
//    public void addKilledMob(LivingEntity entity, UUID killerUUID) {
//        if (entity.isDead()) {
//            killedMobs.putIfAbsent(entity, killerUUID);
//            statsEntityMap.remove(entity);
//        }
//    }
//
//    public static double calculateDamageReduction(double defense) {
//        if (defense >= 0) {
//            double reduction = defense / (defense + 30);
//            return 1 - reduction;
//        } else {
//            return 1 + Math.abs(defense) / (Math.abs(defense) + 15); // Adjustable
//        }
//    }
//
//    public static void removeAllDamageIndicators() {
//        Iterator<TextDisplay> iterator = damageIndicators.iterator();
//        while (iterator.hasNext()) {
//            TextDisplay textDisplay = iterator.next();
//            textDisplay.remove();
//            iterator.remove();
//        }
//    }
//
//    public static void SpawnDamageIndicator(LivingEntity entity, double damage) {
//        if (damage == 0) return;
//        World world = entity.getWorld();
//        TextDisplay textDisplay = world.spawn(entity.getEyeLocation().add(0, 1, 0), TextDisplay.class);
//        damageIndicators.add(textDisplay);
//        textDisplay.setText("§c§l" + (int) damage);
//        textDisplay.setBillboard(Display.Billboard.CENTER);
//        textDisplay.setSeeThrough(false);
//        textDisplay.setShadowed(false);
//        textDisplay.setViewRange(5);
//        textDisplay.setPersistent(false);
//        textDisplay.setBrightness(new Display.Brightness(15, 15));
//        textDisplay.setInterpolationDelay(0);
//        textDisplay.setInterpolationDuration(10); // optional smoothing
//
//        Bukkit.getScheduler().runTaskLater(AtheriumPlugin.getInstance(), textDisplay::remove, 20);
//    }
//
//    public void dealDamage(double damage, Vector hitDirection, double knockbackMultiplier,
//                           LivingEntity attacker) {
//        if (entity.isDead()) return;
//
//        double finalDamage = -damage * calculateDamageReduction(getStat(CustomStat.DEFENCE));
//
//        addOneTimeModifier(CustomStat.HEALTH, finalDamage, 0);
//        SpawnDamageIndicator(entity, finalDamage);
//
//        entity.setVelocity(entity.getLocation().toVector().subtract(hitDirection).
//                normalize().multiply(new Vector(knockbackMultiplier, knockbackMultiplier, knockbackMultiplier)));
//
//        entity.playHurtAnimation(20);
//        if (entity.isDead()) {
//            addKilledMob(entity, attacker.getUniqueId());
//        }
//    }
//
//    public void dealDamage(double damage, LivingEntity attacker) {
//        if (entity.isDead()) return;
//        double finalDamage = -damage * calculateDamageReduction(getStat(CustomStat.DEFENCE));
//
//        addOneTimeModifier(CustomStat.HEALTH, finalDamage, 0);
//        SpawnDamageIndicator(entity, finalDamage);
//
//        entity.setVelocity(entity.getLocation().toVector().
//                subtract(AbilityUtility.getEntityMiddle(attacker).toVector()).normalize());
//
//        entity.playHurtAnimation(20);
//        if (entity.isDead()) {
//            addKilledMob(entity, attacker.getUniqueId());
//        }
//    }
//
//    // handles all the modifiers and stat apply
//    public void periodic() {
//        applyStats();
//        // it's first so all stats will be executed at least once
//        tick++;
//        Iterator<Map.Entry<String, Long>> iterator = timedModifiers.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String, Long> entry = iterator.next();
//            String[] keyParts = entry.getKey().split(":");
//            String key = keyParts[0];
//            CustomStat stat = CustomStat.valueOf(keyParts[1]);
//            if (entry.getValue() <= System.currentTimeMillis() / 1000) { // out of time
//                removeModifier(key, stat);
//                iterator.remove();
//            }
//        }
//    }
//
//    public void applyStats() {
//        if (entity.isDead()) return;
//        for (CustomStat stat : CustomStat.values()) {
//            stat.set(entity, getStat(stat));
//        }
//    }
//
//    public static void updateNonPlayerEntities() {
//        Iterator<Map.Entry<LivingEntity, StatEntity>> iterator = statsEntityMap.entrySet().iterator();
//        while (iterator.hasNext()) {
//            LivingEntity mob = iterator.next().getKey();
//            if (mob instanceof Player) continue;
//            if (mob.isDead()) {
//                iterator.remove();
//            } else {
//                statsEntityMap.get(mob).periodic();
//            }
//        }
//    }
//}