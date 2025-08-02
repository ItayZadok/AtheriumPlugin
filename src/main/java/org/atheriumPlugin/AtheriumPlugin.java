package org.atheriumPlugin;

import org.atheriumPlugin.abilities.AbilityExecuteListener;
import org.atheriumPlugin.combat.CombatListener;
import org.atheriumPlugin.commands.*;
import org.atheriumPlugin.core.GameEngine;
import org.atheriumPlugin.gui.InventoryClickListener;
import org.atheriumPlugin.items.ItemUpdateListener;
import org.atheriumPlugin.utility.StunEffectManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class AtheriumPlugin extends JavaPlugin {

    private static AtheriumPlugin instance;
    private GameEngine gameEngine;

    public static AtheriumPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // Initialize the new game engine
        gameEngine = new GameEngine(this);
        gameEngine.start();

        // Register listeners and commands
        registerListeners();
        registerCommands();

        getLogger().info("AtheriumPlugin has been enabled successfully!");
    }

    private void registerListeners() {
        registerListener(new CombatListener());
        registerListener(new InventoryClickListener());
        registerListener(new ItemUpdateListener());
        registerListener(new AbilityExecuteListener());
        registerListener(new PlayerJoinQuitListener());
    }

    private void registerCommands() {
        registerCommand("abilityMenu", new AbilityMenuCommand());
        registerCommand("stats", new StatMenuCommand());
        registerCommand("setPoints", new SetPointsCommand());
        registerCommand("getItem", new GetItem());
        registerCommand("spawnCustom", new SpawnCustom());
        registerCommand("updateItems", new UpdateItems());
        registerCommand("heal", new HealCommand());
    }

    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public void registerCommand(String commandName, CommandExecutor commandExecutor) {
        Objects.requireNonNull(getCommand(commandName)).setExecutor(commandExecutor);
    }

    @Override
    public void onDisable() {
        // Stop the game engine
        if (gameEngine != null) gameEngine.stop();

        getLogger().info("AtheriumPlugin has been disabled!");
        instance = null;
    }
}
