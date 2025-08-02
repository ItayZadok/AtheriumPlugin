package org.atheriumPlugin.commands;

import org.atheriumPlugin.mobs.CustomMob;
import org.atheriumPlugin.stats.CustomStat;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class SpawnCustom implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (args.length != 1) return false;
        int amount;
        try {
            amount = Integer.parseInt(args[0]);
            if (amount <= 0) {
                player.sendMessage("Amount must be greater than zero.");
                return false;
            }
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount. Please enter a number.");
            return false;
        }

        for (int i = 0; i < amount; i++) {
            LivingEntity entity = (LivingEntity) player.getWorld().
                    spawnEntity(player.getLocation().
                            add(new Vector(0, 0, 5)), EntityType.ZOMBIE);
            if (entity instanceof Zombie mob) mob.setAdult();
            new CustomMob(entity)
                    .setName("&6Bob")
                    .setItem(new ItemStack(Material.DIAMOND_SWORD), EquipmentSlot.HAND, 0)
                    .setItem(new ItemStack(Material.DIAMOND_HELMET), EquipmentSlot.HEAD, 0)
                    .setStat(CustomStat.MAX_HEALTH, 50)
                    .setStat(CustomStat.DAMAGE, 10)
                    .setStat(CustomStat.DEFENCE, 10)
                    .setStat(CustomStat.SPEED, 30)
                    .build();
        }
        return false;
    }
}
