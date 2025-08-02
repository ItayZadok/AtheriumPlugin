//package org.atheriumPlugin.config;
//
//import org.atheriumPlugin.AtheriumPlugin;
//import org.atheriumPlugin.player.PlayerSystem;
//import org.bukkit.configuration.file.FileConfiguration;
//import org.bukkit.configuration.file.YamlConfiguration;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.player.PlayerJoinEvent;
//import org.bukkit.event.player.PlayerQuitEvent;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.HashMap;
//
//public class ConfigManager implements Listener {
//
//    public static final HashMap<ConfigFile, FileConfiguration> configs = new HashMap<>();
//
//    public static void loadConfig(ConfigFile fileName) {
//        AtheriumPlugin plugin = AtheriumPlugin.getInstance();
//        File dataFolder = plugin.getDataFolder();
//
//        // Ensure the data folder exists
//        if (!dataFolder.exists()) {
//            if (!dataFolder.mkdirs()) {
//                plugin.getLogger().severe("Could not create plugin data folder: " + dataFolder.getPath());
//                return;
//            }
//        }
//        File configFile = new File(dataFolder, fileName.getName());
//        // Check if the file exists in the plugin's data folder
//        if (!configFile.exists()) {
//            // Check if the resource exists in the plugin JAR (src/main/resources)
//            if (plugin.getResource(fileName.getName()) != null) {
//                plugin.saveResource(fileName.getName(), false);  // Save it from the JAR to the plugin folder
//            } else {
//                try {
//                    // If it doesn't exist, create a new empty file in the data folder
//                    if (configFile.createNewFile()) {
//                        plugin.getLogger().info("Created new config file: " + configFile.getPath());
//                    } else {
//                        plugin.getLogger().severe("Failed to create new config file: " + configFile.getPath());
//                        return;
//                    }
//                } catch (IOException e) {
//                    plugin.getLogger().severe("Could not create config file " + fileName.getName());
//                    e.printStackTrace(); // Log the stack trace for debugging
//                    return;
//                }
//            }
//        }
//        // Load the configuration
//        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
//        configs.put(fileName, config);
//    }
//
//
//    // Get a loaded custom configuration file
//    public static FileConfiguration getConfig(ConfigFile fileName) {
//        return configs.get(fileName);
//    }
//
//    public static void saveConfig(ConfigFile fileName) {
//        AtheriumPlugin plugin = AtheriumPlugin.getInstance();
//        File configFile = new File(plugin.getDataFolder(), fileName.getName());
//        if (configs.containsKey(fileName)) {
//            try {
//                configs.get(fileName).save(configFile);
//            } catch (IOException e) {
//                plugin.getLogger().severe("Could not save config file " + fileName);
//            }
//        }
//    }
//
//    // reload external changes
//    public static void reloadConfig(ConfigFile fileName) {
//        if (configs.containsKey(fileName)) {
//            File configFile = new File(AtheriumPlugin.getInstance().getDataFolder(), fileName.getName());
//            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
//            configs.put(fileName, config);
//        }
//    }
//
//    @EventHandler
//    public void onExit(PlayerQuitEvent event) {
//        PlayerSystem.getInstance().getPlayerProfile(event.getPlayer()).saveData();
//    }
//
//    @EventHandler
//    public void onJoin(PlayerJoinEvent event) {
//        PlayerSystem.getInstance().getPlayerProfile(event.getPlayer()).reloadItemStats();
//    }
//}
