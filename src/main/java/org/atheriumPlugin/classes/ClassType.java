package org.atheriumPlugin.classes;

import org.atheriumPlugin.abilities.AbilityType;
import org.atheriumPlugin.items.CustomItemRegistry;
import org.atheriumPlugin.items.ItemType;
import org.atheriumPlugin.player.PlayerSystem;
import org.atheriumPlugin.utility.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public enum ClassType {

    KNIGHT(ChatColor.AQUA + "Knight", ItemType.AXE, Material.IRON_AXE, 21,
            Arrays.asList(
                    AbilityType.SHIELD_STUN,
                    AbilityType.CRITICAL_CLEAVE,
                    AbilityType.GUARDIAN_ORBS,
                    AbilityType.UPPERCUT,
                    AbilityType.CHAIN),
            "Resilient warriors with strong defensive skills " +
                    "that use combos to stun their victims to glory"
    ),

    ARCHER(ChatColor.GREEN + "Archer", ItemType.BOW, Material.BOW, 19,
            Arrays.asList(
                    AbilityType.SNIPER_SHOT,
                    AbilityType.EXPLOSIVE_SHOT,
                    AbilityType.DOUBLE_JUMP,
                    AbilityType.AIRBORNE_DAGGERS,
                    AbilityType.SHOT_GUN),
            "Swift fighters excelling in ranged attacks " +
                    "and precision shots to take down foes from a distance in style"
    ),

    ASSASSIN(ChatColor.LIGHT_PURPLE + "Assassin", ItemType.SWORD, Material.GOLDEN_SWORD, 20,
            Arrays.asList(
                    AbilityType.COMBO_STAR,
                    AbilityType.ESCAPE_ARTIST,
                    AbilityType.BLADE_DANCE,
                    AbilityType.EXECUTION_FLOW,
                    AbilityType.AXE_KICK),
            "Dark avengers who thrive on the edge of life and death " +
                    "feeding from their enemies' souls to unleash impending doom"
    ),

    DEFAULT("Default", ItemType.GENERIC, Material.AIR, 0, new ArrayList<>(), "");

    private final String displayName;
    private final ItemType itemType;
    private final Material icon;
    private final List<AbilityType> abilities;
    private final String description;
    private final int id;

    ClassType(String displayName, ItemType itemType, Material icon, int id, List<AbilityType> abilities, String description) {
        this.displayName = displayName;
        this.itemType = itemType;
        this.icon = icon;
        this.id = id;
        this.abilities = abilities;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public List<String> getDescription() {
        return ItemBuilder.toLore(description, ChatColor.DARK_GRAY, 20);
    }

    public Material getIcon() {
        return icon;
    }

    public int getIconId() {
        return id;
    }

    public List<AbilityType> getAbilities() {
        return abilities;
    }

    public static boolean isClassItem(Player player, ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ClassType classType = PlayerSystem.getInstance().getAbilityManager(player).getClassType();
        if (classType == null || classType.equals(ClassType.DEFAULT)) return false;
        ItemType classItemType = classType.getItemType();
        int customModelData;
        try {
            customModelData = Objects.requireNonNull(item.getItemMeta()).getCustomModelData();
        } catch (NullPointerException e) {
            return false;
        }
        return CustomItemRegistry.getItem(customModelData).itemType().equals(classItemType);
    }
}
