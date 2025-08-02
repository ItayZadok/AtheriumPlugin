package org.atheriumPlugin.commands;

import org.atheriumPlugin.items.ItemSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UpdateItems implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((sender instanceof Player player)) ItemSystem.updateInventory(player.getInventory());
        return false;
    }
}
