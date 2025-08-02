package org.atheriumPlugin.commands;

import org.atheriumPlugin.player.PlayerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetPointsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (args.length < 1) {
            player.sendMessage("Usage: /setpoints <points>");
            return false;
        }
        // Attempt to parse the points argument
        try {
            int points = Integer.parseInt(args[0]);
            if (points < 0) {
                player.sendMessage("Ability points cannot be negative.");
                return false;
            }
            PlayerSystem.getInstance().getAbilityManager(player).setAbilityPoints(points);
            player.sendMessage("Your ability points have been set to " + points);
            return true;
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid number");
            return false;
        }
    }
}
