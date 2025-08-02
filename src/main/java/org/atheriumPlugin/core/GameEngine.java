package org.atheriumPlugin.core;

import org.atheriumPlugin.AtheriumPlugin;
import org.atheriumPlugin.abilities.AbilitySystem;
import org.atheriumPlugin.combat.CombatSystem;
import org.atheriumPlugin.config.ConfigFile;
import org.atheriumPlugin.config.ConfigManager;
import org.atheriumPlugin.items.ItemSystem;
import org.atheriumPlugin.player.PlayerSystem;
import org.atheriumPlugin.stats.StatSystem;
import org.atheriumPlugin.tablist.TabListManager;
import org.atheriumPlugin.utility.StunEffectManager;
import org.bukkit.plugin.Plugin;

/**
 * Core game engine that coordinates all systems.
 * Implements the Facade pattern to provide a clean interface to the plugin's functionality.
 */
public class GameEngine {

    private final Plugin plugin;
    private final GameLoop gameLoop;

    private final PlayerSystem playerSystem;
    private final StatSystem statSystem;
    private final AbilitySystem abilitySystem;

    public GameEngine(AtheriumPlugin plugin) {
        this.plugin = plugin;

        // Initialize systems in dependency order
        new StunEffectManager();
        this.playerSystem = PlayerSystem.getInstance();
        this.statSystem = StatSystem.getInstance();
        this.abilitySystem = AbilitySystem.getInstance();
        this.gameLoop = new GameLoop();
    }

    public void start() {
        ConfigManager.getInstance().loadAllConfigs();
        ItemSystem.loadAllItems();
        abilitySystem.registerAllAbilities();
        TabListManager.updateTab();
        gameLoop.start();
        plugin.getLogger().info("AtheriumPlugin game engine started successfully");
    }

    public void stop() {
        gameLoop.stop();

        // cleanup
        ConfigManager.getInstance().reloadConfig(ConfigFile.PLAYER_ABILITY_CONFIG);
        CombatSystem.getInstance().cleanup();
        abilitySystem.cleanup();
        statSystem.cleanup();
        playerSystem.cleanup();
        plugin.getLogger().info("AtheriumPlugin game engine stopped");
    }
}