package org.atheriumPlugin.items;

import org.atheriumPlugin.stats.CustomStat;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomItemLoader {

    public static List<CustomItem> loadItems(FileConfiguration config) {
        List<CustomItem> items = new ArrayList<>();
        ConfigurationSection section = config.getConfigurationSection("items");
        if (section == null) return items;

        for (String key : section.getKeys(false)) {
            CustomItem item = loadItem(config, key);
            if (item != null) items.add(item);
        }

        return items;
    }

    private static CustomItem loadItem(FileConfiguration config, String key) {
        Material material = Material.getMaterial(
                config.getString("items." + key + ".material", ""));
        if (material == null) return null;

        ItemRarity rarity = parseEnum(ItemRarity.class,
                config.getString("items." + key + ".rarity"), ItemRarity.COMMON);
        ItemType type = parseEnum(ItemType.class,
                config.getString("items." + key + ".type"), ItemType.GENERIC);

        String displayName = config.getString("items." + key + ".displayName", key);
        String description = config.getString("items." + key + ".description", "");

        HashMap<CustomStat, Double> stats
                = parseStats(config, "items." + key + ".stats");
        HashMap<CustomStat, Double> percentStats
                = parsePercentStats(config, "items." + key + ".stats");

        int id = parseId(key);

        return new CustomItem(id, material, stats, percentStats, rarity, description, displayName, type);
    }

    private static <T extends Enum<T>> T parseEnum(Class<T> enumClass, String value, T fallback) {
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (Exception e) {
            return fallback;
        }
    }

    private static HashMap<CustomStat, Double> parseStats(FileConfiguration config, String path) {
        HashMap<CustomStat, Double> stats = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) return stats;

        for (String stat : section.getKeys(false)) {
            try {
                stats.put(CustomStat.valueOf(stat), config.getDouble(path + "." + stat));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return stats;
    }

    private static HashMap<CustomStat, Double> parsePercentStats(FileConfiguration config, String path) {
        HashMap<CustomStat, Double> stats = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) return stats;

        for (String stat : section.getKeys(false)) {
            try {
                if (!config.getString(path + "." + stat).contains("%")) continue;
                String raw = config.getString(path + "." + stat, "0").replace("%", "");
                stats.put(CustomStat.valueOf(stat), Double.parseDouble(raw));
            } catch (Exception ignored) {
            }
        }
        return stats;
    }

    private static int parseId(String key) {
        try {
            return Integer.parseInt(key);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
