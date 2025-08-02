package org.atheriumPlugin.commands;

import org.atheriumPlugin.gui.inventories.ClassSelectionMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AbilityMenuCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if ((commandSender instanceof Player player)) ClassSelectionMenu.openInventory(player);
        return true;
    }
}
