package org.atheriumPlugin.abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

public abstract class AbilityBase implements Listener {

    // called once when the ability is executed
    public void execute(Player player, Event genericEvent) {};

    // called every tick
    public void periodic() {};

    // called when game starts
    public void onRegister() {};

    // called when game ends
    public void onDisable() {};

    // called before execute to check if the ability can execute
    public boolean canActivate(Player player, Event genericEvent) {
        return true;
    }
}