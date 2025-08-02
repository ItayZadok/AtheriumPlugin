package org.atheriumPlugin.config;

public enum ConfigFile {

    PLAYER_ABILITY_CONFIG("playerAbilityConfig.yml"),
    ITEM_CONFIG("itemConfig.yml");

    private final String name;

    ConfigFile(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

