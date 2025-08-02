package org.atheriumPlugin.stats;

import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TextDisplay;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class HealthBarManager {

    private static final ConcurrentHashMap<LivingEntity, TextDisplay> activeBars =
            new ConcurrentHashMap<>();

    /**
     * Updates / spawns a health bar for an entity
     */
    public static void updateBar(LivingEntity entity) {
        if (!entity.isValid()) {
            removeBar(entity);
            return;
        }

        TextDisplay display = activeBars.computeIfAbsent(entity, HealthBarManager::createBar);
        updateBarText(entity, display);
        updateBarLocation(entity, display);
    }

    private static void updateBarText(LivingEntity entity, TextDisplay textDisplay) {
        String healthStr = String.format("%.1f", entity.getHealth());
        String maxHealthStr = String.format("%.1f", entity.getMaxHealth());
        textDisplay.setText("§c❤ §l" + healthStr + "§7/§r" + maxHealthStr);
    }

    private static void updateBarLocation(LivingEntity entity, TextDisplay textDisplay) {
        textDisplay.teleport(entity.getEyeLocation().add(0, 0.75, 0)); // fallback
    }

    private static TextDisplay createBar(LivingEntity entity) {
        World world = entity.getWorld();
        TextDisplay textDisplay = world.spawn(
                entity.getEyeLocation().add(0, 0.75, 0),
                TextDisplay.class
        );

        // Configure the display
        textDisplay.setTeleportDuration(3); // smooth movement
        updateBarText(entity, textDisplay);
        textDisplay.setBillboard(Display.Billboard.CENTER);
        textDisplay.setSeeThrough(false);
        textDisplay.setShadowed(false);
        textDisplay.setViewRange(5);
        textDisplay.setPersistent(true);
        textDisplay.setBrightness(new Display.Brightness(15, 15));
        textDisplay.setInterpolationDelay(0);
        textDisplay.setInterpolationDuration(10);
        return textDisplay;
    }

    private static void removeBar(LivingEntity entity) {
        if (!activeBars.containsKey(entity)) return;
        activeBars.get(entity).remove();
        activeBars.remove(entity);
    }

    /**
     * Clean up all indicators
     */
    public static void cleanup() {
        activeBars.values().forEach(bar -> {
            if (bar.isValid()) {
                bar.remove();
            }
        });
        activeBars.clear();
    }

    public static void updateAll(Set<LivingEntity> entities) {
        for (LivingEntity entity : entities) {
            updateBar(entity);
        }
    }

    /**
     * Get the number of active indicators
     */
    public static int getActiveBarCount() {
        return activeBars.size();
    }
}
