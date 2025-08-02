package org.atheriumPlugin.items;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collection;
import java.util.HashMap;

public class CustomItemRegistry {

    private static final HashMap<Integer, CustomItem> items = new HashMap<>();

    public static void register(CustomItem item) {
        items.put(item.itemId(), item);
    }

    public static void loadAllFromConfig(FileConfiguration config) {
        for (CustomItem item : CustomItemLoader.loadItems(config)) {
            register(item);
        }
    }

    public static int getItemAmount() {
        return items.size();
    }

    public static CustomItem getItem(int id) {
        return items.get(id);
    }

    public static Collection<CustomItem> getAllItems() {
        return items.values();
    }
}

