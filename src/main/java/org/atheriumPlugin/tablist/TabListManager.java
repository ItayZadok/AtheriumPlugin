package org.atheriumPlugin.tablist;

import org.atheriumPlugin.player.PlayerSystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TabListManager {

    public static void updateTab() {
        for (Player player : Bukkit.getOnlinePlayers()) {

            String className = ChatColor.stripColor(PlayerSystem.getInstance().
                    getAbilityManager(player).getClassType().getDisplayName().toLowerCase());

            player.setPlayerListName(
                    ChatColor.GRAY + "<" + className + ">" +
                    ChatColor.WHITE + player.getDisplayName());
        }
    }
}
