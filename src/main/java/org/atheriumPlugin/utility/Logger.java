package org.atheriumPlugin.utility;

import org.atheriumPlugin.AtheriumPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

public class Logger {

    public static <T> void log(T message) {
        Bukkit.broadcastMessage(String.valueOf(message));
    }

    public static <T> void console(T message) {
        Bukkit.getConsoleSender().sendMessage("[Logger] " + message);
    }

    public static <T> void showActionBar(Supplier<T> message) {
        Bukkit.getScheduler().runTaskTimer(AtheriumPlugin.getInstance(),
                () -> showActionBar(message.get()), 0, 1);
    }

    public static <T> void showActionBar(T message) {
        Player player = Bukkit.getPlayer("Itay_666");
        if (player == null) return;

        player.spigot().sendMessage(
                net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                new net.md_5.bungee.api.chat.TextComponent(String.valueOf(message))
        );
    }
}
