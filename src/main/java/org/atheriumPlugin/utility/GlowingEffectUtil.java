package org.atheriumPlugin.utility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Objects;

public class GlowingEffectUtil {

    private static final Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();

    public static void setGlowing(Entity entity, ChatColor color, boolean glowing) {
        Team team = scoreboard.getTeam(color.name());
        if (glowing) {
            if (team == null) {
                team = scoreboard.registerNewTeam(color.name());
                team.setColor(color);
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            }
            team.addEntry(entity.getUniqueId().toString());
            entity.setGlowing(true);
        } else {
            if (team != null) {
                team.removeEntry(entity.getUniqueId().toString());
            }
            entity.setGlowing(false);
        }
    }
}

