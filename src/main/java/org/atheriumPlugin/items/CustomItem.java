package org.atheriumPlugin.items;

import org.atheriumPlugin.stats.CustomStat;
import org.bukkit.Material;

import java.util.HashMap;

public record CustomItem(int itemId, Material material, HashMap<CustomStat, Double> valueStats,
                         HashMap<CustomStat, Double> percentStats, ItemRarity rarity, String description,
                         String displayName, ItemType itemType) {
}
