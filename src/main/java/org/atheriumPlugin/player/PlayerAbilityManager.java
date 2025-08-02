package org.atheriumPlugin.player;

import org.atheriumPlugin.abilities.AbilityType;
import org.atheriumPlugin.classes.ClassType;
import org.atheriumPlugin.config.ConfigFile;
import org.atheriumPlugin.config.ConfigManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class PlayerAbilityManager {

    private final UUID playerUUID;
    private ClassType classType;
    private final HashSet<PlayerAbility> unlockedAbilities;
    private int abilityPoints;

    public PlayerAbilityManager(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.unlockedAbilities = new HashSet<>();
        loadFromConfig();
    }

    public PlayerAbility getPlayerAbility(AbilityType abilityData) {
        return getUnlockedAbilities().stream()
                .filter(ability -> ability.getAbilityType().equals(abilityData))
                .findFirst()
                .orElse(null);
    }

    public void addAbility(AbilityType abilityType) {
        if (abilityPoints > 0) {
            unlockedAbilities.add(new PlayerAbility(abilityType, playerUUID, abilityType.getDefaultInputTypes()));
            abilityPoints--;
        }
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public void setAbilityPoints(int abilityPoints) {
        this.abilityPoints = abilityPoints;
    }

    public ClassType getClassType() {
        return classType;
    }

    public int getAbilityPoints() {
        return abilityPoints;
    }

    public Set<PlayerAbility> getUnlockedAbilities() {
        return unlockedAbilities;
    }

    public boolean isAbilityUnlocked(AbilityType abilityType) {
        for (PlayerAbility ability : unlockedAbilities)
            if (ability.getAbilityType().equals(abilityType)) return true;
        return false;
    }

    public void removeAbility(AbilityType abilityType) {
        unlockedAbilities.removeIf(playerAbility
                -> playerAbility.getAbilityType().equals(abilityType));
        abilityPoints++;
    }

    public void resetAbilities() {
        abilityPoints += unlockedAbilities.size();
        unlockedAbilities.clear();
    }

    // Load player data from config
    private void loadFromConfig() {
        FileConfiguration config = ConfigManager.getInstance()
                .getConfig(ConfigFile.PLAYER_ABILITY_CONFIG);

        String path = "players." + playerUUID;
        classType = config.contains(path + ".classType") ?
                ClassType.valueOf(config.getString(path + ".classType")) :
                ClassType.DEFAULT;

        abilityPoints = config.getInt(path + ".abilityPoints", 0);
        List<String> unlockedAbilityNames = config.getStringList(path + ".unlockedAbilities");
        List<String> validAbilities = new ArrayList<>();

        for (String abilityName : unlockedAbilityNames) {
            try {
                AbilityType abilityType = AbilityType.valueOf(abilityName);
                unlockedAbilities.add(new PlayerAbility(abilityType, playerUUID, abilityType.getDefaultInputTypes()));
                validAbilities.add(abilityName);
            } catch (IllegalArgumentException e) {
                System.out.println("Ability name not found: " + e);
                abilityPoints++; // if ability not found
            }
        }

        config.set(path + ".unlockedAbilities", validAbilities);
        ConfigManager.getInstance().loadConfig(ConfigFile.PLAYER_ABILITY_CONFIG);
    }

    // Save player data to config
    public void saveToConfig() {
        FileConfiguration config = ConfigManager.getInstance().getConfig(ConfigFile.PLAYER_ABILITY_CONFIG);
        String path = "players." + playerUUID.toString();
        config.set(path + ".classType", classType.toString());
        config.set(path + ".abilityPoints", abilityPoints);
        config.set(path + ".unlockedAbilities", unlockedAbilities.stream()
                .map(playerAbility -> playerAbility.getAbilityType().toString())
                .toList());
        ConfigManager.getInstance().saveConfig(ConfigFile.PLAYER_ABILITY_CONFIG);
    }
}
