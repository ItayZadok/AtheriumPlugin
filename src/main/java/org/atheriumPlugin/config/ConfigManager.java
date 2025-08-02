package org.atheriumPlugin.config;

import org.atheriumPlugin.AtheriumPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralized configuration management system.
 * Replaces the old ConfigManager with better organization and error handling.
 */
public class ConfigManager {

    private final Plugin plugin;
    private final Map<ConfigFile, FileConfiguration> configs = new HashMap<>();

    private static ConfigManager instance;

    public static ConfigManager getInstance() {
        if (instance == null) instance = new ConfigManager();
        return instance;
    }

    private ConfigManager() {
        this.plugin = AtheriumPlugin.getInstance();
    }

    /**
     * Load all configuration files
     */
    public void loadAllConfigs() {
        for (ConfigFile configFile : ConfigFile.values()) {
            loadConfig(configFile);
        }
    }

    /**
     * Load a specific configuration file
     */
    public void loadConfig(ConfigFile configFile) {
        File dataFolder = plugin.getDataFolder();

        // Ensure the data folder exists
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            plugin.getLogger().severe("Could not create plugin data folder: " + dataFolder.getPath());
            return;
        }

        File file = new File(dataFolder, configFile.getName());

        // Check if the file exists in the plugin's data folder
        if (!file.exists()) {
            // Check if the resource exists in the plugin JAR
            if (plugin.getResource(configFile.getName()) != null) {
                plugin.saveResource(configFile.getName(), false);
            } else {
                try {
                    if (!file.createNewFile()) {
                        plugin.getLogger().severe("Failed to create new config file: " + file.getPath());
                        return;
                    }
                    plugin.getLogger().info("Created new config file: " + file.getPath());
                } catch (IOException e) {
                    plugin.getLogger().severe("Could not create config file " + configFile.getName());
                    e.printStackTrace();
                    return;
                }
            }
        }

        // Load the configuration
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        configs.put(configFile, config);
        plugin.getLogger().info("Loaded configuration: " + configFile.getName());
    }

    /**
     * Get a loaded configuration file
     */
    public FileConfiguration getConfig(ConfigFile configFile) {
        return configs.get(configFile);
    }

    /**
     * Save a configuration file
     */
    public void saveConfig(ConfigFile configFile) {
        File file = new File(plugin.getDataFolder(), configFile.getName());
        FileConfiguration config = configs.get(configFile);

        if (config != null) {
            try {
                config.save(file);
                plugin.getLogger().info("Saved configuration: " + configFile.getName());
            } catch (IOException e) {
                plugin.getLogger().severe("Could not save config file " + configFile.getName());
                e.printStackTrace();
            }
        }
    }

    /**
     * Reload a configuration file
     */
    public void reloadConfig(ConfigFile configFile) {
        File file = new File(plugin.getDataFolder(), configFile.getName());
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        configs.put(configFile, config);
        plugin.getLogger().info("Reloaded configuration: " + configFile.getName());
    }

    /**
     * Save all configuration files
     */
    public void saveAllConfigs() {
        for (ConfigFile configFile : ConfigFile.values()) {
            saveConfig(configFile);
        }
    }

    /**
     * Get the number of loaded configurations
     */
    public int getLoadedConfigCount() {
        return configs.size();
    }

    /**
     * Check if a configuration is loaded
     */
    public boolean isConfigLoaded(ConfigFile configFile) {
        return configs.containsKey(configFile);
    }
}