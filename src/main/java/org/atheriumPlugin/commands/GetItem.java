package org.atheriumPlugin.commands;

import org.atheriumPlugin.gui.inventories.CustomItemsMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetItem implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((sender instanceof Player player)) CustomItemsMenu.openInventory(player);
        return false;
    }
}
