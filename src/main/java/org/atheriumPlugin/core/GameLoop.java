package org.atheriumPlugin.core;

import org.atheriumPlugin.AtheriumPlugin;
import org.atheriumPlugin.abilities.AbilitySystem;
import org.atheriumPlugin.combat.CombatSystem;
import org.atheriumPlugin.player.PlayerSystem;
import org.atheriumPlugin.stats.HealthBarManager;
import org.atheriumPlugin.stats.StatSystem;
import org.atheriumPlugin.tablist.TabListManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Centralized game loop that manages all periodic updates.
 * Replaces the old PlayerScheduler with a more efficient and organized approach.
 */
public class GameLoop {

    private BukkitTask gameLoopTask;
    private final AtomicLong tickCounter = new AtomicLong(0);

    // Performance tuning constants
    private static final int STAT_UPDATE_INTERVAL = 1; // Every tick
    private static final int COMBAT_UPDATE_INTERVAL = 1; // Every tick
    private static final int ABILITY_UPDATE_INTERVAL = 1; // Every tick
    private static final int TAB_LIST_UPDATE_INTERVAL = 2; // Every tick
    private static final int CLEANUP_INTERVAL = 100; // Every 5 seconds (100 ticks)

    private static final StatSystem statSystem = StatSystem.getInstance();
    private static final CombatSystem combatSystem = CombatSystem.getInstance();
    private static final PlayerSystem playerSystem = PlayerSystem.getInstance();
    private static final AbilitySystem abilitySystem = AbilitySystem.getInstance();

    public void start() {
        gameLoopTask = Bukkit.getScheduler().
                runTaskTimer(AtheriumPlugin.getInstance(), this::tick, 1L, 1L);
    }

    public void stop() {
        if (gameLoopTask != null) {
            gameLoopTask.cancel();
            gameLoopTask = null;
        }
    }

    private void tick() {
        long currentTick = tickCounter.incrementAndGet();

        // Update stats system
        if (currentTick % STAT_UPDATE_INTERVAL == 0) {
            statSystem.updateAll();
        }

        // Update combat system
        if (currentTick % COMBAT_UPDATE_INTERVAL == 0) {
            combatSystem.update();
        }

        // Update ability system
        if (currentTick % ABILITY_UPDATE_INTERVAL == 0) {
            abilitySystem.updateAll();
        }

        if (currentTick % TAB_LIST_UPDATE_INTERVAL == 0) {
            TabListManager.updateTab();
        }

        // Update player systems
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerSystem.updatePlayer(player);
        }

        // Periodic cleanup
        if (currentTick % CLEANUP_INTERVAL == 0) {
            performCleanup();
        }
    }

    private void performCleanup() {
        StatSystem.getInstance().cleanupDeadEntities();
    }
} 