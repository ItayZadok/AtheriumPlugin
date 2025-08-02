package org.atheriumPlugin.abilities.archer;

import org.atheriumPlugin.abilities.AbilityBase;
import org.atheriumPlugin.utility.AbilityUtility;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class AirborneDaggersAbility extends AbilityBase {

    private final List<Dagger> daggerList = new ArrayList<>();
    private static final double RADIUS = 1.5;
    private static final int COUNT = 10;

    @Override
    public void execute(Player player, Event genericEvent) {
        Location center = AbilityUtility.getEntityMiddle(player);
        player.setVelocity(new Vector(0, 0.75, 0));
        player.playSound(player, Sound.ITEM_TRIDENT_RIPTIDE_1, 0.75F, 1);
        spawnDaggers(center);
    }

    private void spawnDaggers(Location center) {
        World world = center.getWorld();
        if (world == null) return;
        daggerList.removeIf(Dagger::isDestroyed);

        for (Vector location : AbilityUtility.generatePointCircle(center.toVector(), RADIUS, COUNT)) {
            Vector direction = center.clone().add(new Vector(0, 3.5, 0)).subtract(location).toVector().normalize();
            daggerList.add(new Dagger(location.toLocation(world), direction.multiply(-1), center));
        }
        for (Vector location : AbilityUtility.generatePointCircle(center.toVector(), RADIUS / 2, COUNT / 2)) {
            Vector direction = center.clone().add(new Vector(0, 3.5, 0)).subtract(location).toVector().normalize();
            daggerList.add(new Dagger(location.toLocation(world), direction.multiply(-1), center));
        }
    }
}