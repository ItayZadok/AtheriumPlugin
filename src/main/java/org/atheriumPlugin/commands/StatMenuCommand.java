package org.atheriumPlugin.commands;

import org.atheriumPlugin.gui.inventories.PlayerStatsMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class StatMenuCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {

            LivingEntity target = player;

            if (args.length == 1) {
                Player p = Bukkit.getPlayerExact(args[0]);
                if (p != null) {
                    target = p;
                }
                else {
                    player.sendMessage("No such username");
                    return true;
                }
            }

            PlayerStatsMenu.openInventory(player, target);
        }
        return true;
    }
}
