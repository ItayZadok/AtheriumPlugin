package org.atheriumPlugin.player;

import org.atheriumPlugin.items.CustomItem;
import org.atheriumPlugin.stats.CustomStat;
import org.atheriumPlugin.stats.EntityStats;
import org.atheriumPlugin.stats.StatSystem;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerItemStatsManager {

    private final Player player;
    private final HashMap<CustomStat, Double> flatStats = new HashMap<>();
    private final HashMap<CustomStat, Double> precentStats = new HashMap<>();

    public PlayerItemStatsManager(Player player) {
        this.player = player;
    }

    public double getValueStat(CustomStat stat) {
        return flatStats.getOrDefault(stat, 0D);
    }

    public double getPercentStat(CustomStat stat) {
        return precentStats.getOrDefault(stat, 0D);
    }

    private void applyItemStats() {
        EntityStats playerStats = StatSystem.getInstance().getStats(player);
        for (CustomStat stat : CustomStat.values()) {
            // remove old stats
            playerStats.removeModifier("equipment_flat" + stat, stat);
            playerStats.removeModifier("equipment_perc" + stat, stat);

            // add new stats
            playerStats.addPermanentModifier(
                    "equipment_flat" + stat, stat, getValueStat(stat), 0);
            playerStats.addPermanentModifier(
                    "equipment_perc" + stat, stat, 0, getPercentStat(stat));
        }
    }

    public void updateStatsFromItems(PlayerItemManager itemManager) {
        flatStats.clear();
        precentStats.clear();

        for (CustomItem item : itemManager.getItems()) {
            if (item == null) continue;
            for (Map.Entry<CustomStat, Double> entry : item.valueStats().entrySet()) {
                flatStats.put(entry.getKey(), entry.getValue() + getValueStat(entry.getKey()));
            }
            for (Map.Entry<CustomStat, Double> entry : item.percentStats().entrySet()) {
                precentStats.put(entry.getKey(), entry.getValue() + getPercentStat(entry.getKey()));
            }
        }

        applyItemStats();
    }
}

