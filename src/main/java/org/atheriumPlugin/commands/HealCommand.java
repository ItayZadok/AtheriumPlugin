package org.atheriumPlugin.commands;

import org.atheriumPlugin.stats.CustomStat;
import org.atheriumPlugin.stats.StatSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        player.setFoodLevel(20);
        StatSystem.getInstance().getStats(player).addOneTimeModifier(
                CustomStat.HEALTH, StatSystem.getStat(player, CustomStat.MAX_HEALTH), 0
        );
        return true;
    }
}
