//package org.atheriumPlugin;
//
//import org.atheriumPlugin.abilities.AbilityData;
//import org.atheriumPlugin.player.PlayerAbility;
//import org.atheriumPlugin.player.PlayerManager;
//import org.atheriumPlugin.stats.StatEntity;
//import org.bukkit.Bukkit;
//import org.bukkit.entity.Player;
//import org.bukkit.event.Event;
//import org.bukkit.scheduler.BukkitTask;
//
//import java.util.concurrent.ConcurrentHashMap;
//
//public class PlayerScheduler {
//
//    private final static ConcurrentHashMap<Player, PlayerScheduler> playerSchedulers = new ConcurrentHashMap<>();
//    private static BukkitTask mainLoopTask;
//
//    private final PlayerManager playerManager;
//    private final ConcurrentHashMap<PlayerAbility, Event> abilitiesOnQueue = new ConcurrentHashMap<>();
//
//    private PlayerScheduler(Player player) {
//        playerManager = PlayerManager.getProfile(player);
//    }
//
//    public static void mainLoop() {
//        mainLoopTask = Bukkit.getScheduler().runTaskTimer(AtheriumPlugin.getInstance(), () -> {
//            StatEntity.updateNonPlayerEntities();
//            for (Player player : Bukkit.getOnlinePlayers()) {
//                getPlayerScheduler(player).periodic();
//            }
//            runAllAbilities();
//
//        }, 1, 0);
//    }
//
//    public static void stopMainLoop() {
//        mainLoopTask.cancel();
//    }
//
//    private static PlayerScheduler getPlayerScheduler(Player player) {
//        return playerSchedulers.computeIfAbsent(player, PlayerScheduler::new);
//    }
//
//    private static void runAllAbilities() {
//        for (AbilityData abilityData : AbilityData.values()) {
//            abilityData.getAbilityBase().periodic();
//        }
//    }
//
//    public static void queueAbility(Player player, PlayerAbility playerAbility, Event event) {
//        getPlayerScheduler(player).abilitiesOnQueue.put(playerAbility, event);
//    }
//
//    private void periodic() {
//        executeAllAbilities();
//        playerManager.updateStats();
//    }
//
//    private void executeAllAbilities() {
//        for (PlayerAbility ability : abilitiesOnQueue.keySet()) {
//            ability.execute(abilitiesOnQueue.get(ability));
//            abilitiesOnQueue.remove(ability);
//        }
//    }
//}
