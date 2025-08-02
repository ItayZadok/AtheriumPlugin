package org.atheriumPlugin.items;

import org.atheriumPlugin.stats.CustomStat;
import org.atheriumPlugin.utility.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomItemRenderer {

    private static final HashMap<Integer, ItemStack> itemCache = new HashMap<>();

    public static ItemStack render(CustomItem item) {
        if (itemCache.containsKey(item.itemId())) {
            return itemCache.get(item.itemId());
        }

        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Rarity: "
                + item.rarity().getDisplayName() + " " + item.itemType().getDisplayName());
        lore.add("");

        // stats
        for (CustomStat stat : CustomStat.values()) {
            String line = formatStatLine(stat, item);
            if (line != null) lore.add(line);
        }

        lore.add("");
        lore.addAll(ItemBuilder.toLore(item.description(), ChatColor.DARK_GRAY, 25));
        lore.add("");

        ItemStack stack = new ItemBuilder(item.material())
                .setDisplayName(item.displayName())
                .setCustomModelData(item.itemId())
                .setLore(lore)
                .setUnbreakable(true)
                .fixCustomAttackSpeed()
                .build();

        itemCache.put(item.itemId(), stack);
        return stack;
    }

    private static String formatStatLine(CustomStat stat, CustomItem item) {
        boolean hasValue = item.valueStats().getOrDefault(stat, 0.0) != 0;
        boolean hasPercent = item.percentStats().getOrDefault(stat, 0.0) != 0;
        if (!hasValue && !hasPercent) return null;

        String line = stat.getDisplayName() + ": " + stat.getColor();

        if (hasValue) {
            double value = item.valueStats().get(stat);
            line += String.format("%s%.1f ", (value > 0 ? "+" : ""), value);
        }

        if (hasPercent) {
            double percent = item.percentStats().get(stat);
            line += String.format("%s%.1f%%", (percent > 0 ? "+" : ""), percent);
        }

        return line;
    }
}
