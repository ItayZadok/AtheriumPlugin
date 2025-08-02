package org.atheriumPlugin.stats;

import org.atheriumPlugin.AtheriumPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TextDisplay;

import java.util.LinkedList;
import java.util.List;

/**
 * Manages damage indicators independently of the stat system.
 */
public class DamageIndicatorManager {

    private final List<TextDisplay> activeIndicators = new LinkedList<>();
    private static final long INDICATOR_LIFETIME = 20;

    /**
     * Spawn a damage indicator for an entity
     */
    public void spawnDamageIndicator(LivingEntity entity, double damage) {
        if (damage == 0 || entity.isDead()) return;

        World world = entity.getWorld();
        TextDisplay textDisplay = world.spawn(
                entity.getEyeLocation().add(0, 1, 0),
                TextDisplay.class
        );

        // Configure the display
        textDisplay.setText("§c§l-" + (int) damage);
        textDisplay.setBillboard(Display.Billboard.CENTER);
        textDisplay.setSeeThrough(false);
        textDisplay.setShadowed(false);
        textDisplay.setViewRange(5);
        textDisplay.setPersistent(false);
        textDisplay.setBrightness(new Display.Brightness(15, 15));
        textDisplay.setInterpolationDelay(0);
        textDisplay.setInterpolationDuration(10);

        // Add to active list
        activeIndicators.add(textDisplay);

        // Schedule removal
        Bukkit.getScheduler().runTaskLater(
                AtheriumPlugin.getInstance(),
                () -> removeIndicator(textDisplay),
                INDICATOR_LIFETIME
        );
    }

    /**
     * Remove a specific indicator
     */
    private void removeIndicator(TextDisplay indicator) {
        if (indicator.isValid()) {
            indicator.remove();
        }
        activeIndicators.remove(indicator);
    }

    /**
     * Clean up all indicators
     */
    public void cleanup() {
        activeIndicators.forEach(indicator -> {
            if (indicator.isValid()) {
                indicator.remove();
            }
        });
        activeIndicators.clear();
    }

    /**
     * Get the number of active indicators
     */
    public int getActiveIndicatorCount() {
        return activeIndicators.size();
    }
} 