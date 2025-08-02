package org.atheriumPlugin.abilities;

import org.atheriumPlugin.AtheriumPlugin;
import org.atheriumPlugin.abilities.archer.*;
import org.atheriumPlugin.abilities.assassin.*;
import org.atheriumPlugin.abilities.knight.*;
import org.bukkit.Material;

import java.util.Set;

import static org.atheriumPlugin.abilities.InputType.*;

public enum AbilityType {

    SHIELD_STUN("Shield Stun", 7, new ShieldStunAbility(), Material.SHIELD,
            "When blocking an attack, stun and knock back nearby enemies",
            "The Stun lasts 3 seconds", CHARGE_SHIELD),

    CRITICAL_CLEAVE("Critical Cleave", 2, new CriticalCleaveAbility(), Material.GOLDEN_AXE,
            "Critical attacks deal part of their damage to nearby mobs",
            "25% of the target damage", CRITICAL),

    GUARDIAN_ORBS("Guardian Orbs", 20, new GuardianOrbsAbility(), Material.GOLDEN_APPLE,
            "Creates 3 light orbs that slow down nearby enemies",
            "eel", Q),

    UPPERCUT("Uppercut", 7, new UppercutAbility(), Material.NETHER_WART,
            "Elevates you and a nearby enemy, slamming it to the ground upon impact, dealing massive damage",
            "The damage is 2x a normal attack", SHIFT, AIR),

    CHAIN("Chain", 10, new ChainAbility(), Material.CHAIN,
            "Releases a chain that hooks an enemy and drags it towards you",
            "The chain radius is 15 blocks", F),

    SNIPER_SHOT("Sniper Shot", 4, new SniperShotAbility(), Material.TRIDENT,
            "Shoots a high-damage sniper shot that deals more damage the further away the target is",
            "The max damage is 2x the minimum", LEFT_CLICK),

    EXPLOSIVE_SHOT("Explosive Shot", 5, new ExplosiveShotAbility(), Material.TNT,
            "Shoots an explosive bolt that deals massive damage and recoils upon impact",
            "The explosion radius is 3 blocks", F),

    DOUBLE_JUMP("Double Jump", 5, new DoubleJumpAbility(), Material.MAGENTA_GLAZED_TERRACOTTA,
            "a small double jump that gives you speed",
            "the speed lasts 10 seconds", DOUBLE_SPACE),

    AIRBORNE_DAGGERS("Airborne Daggers", 15, new AirborneDaggersAbility(), Material.SHEARS,
            "While airborne shoot daggers to the ground, slowing nearby enemies",
            "the slowness is 25% speed reduction", SHIFT, AIR),

    SHOT_GUN("Shot Gun", 7, new ShotGunAbility(), Material.CROSSBOW,
            "Shoots a powerful multi-shot pierce attack.",
            "The Maximum pierce is 3 enemies", SHOOT),

    COMBO_STAR("Combo Star", 10, new ComboStarAbility(), Material.BLAZE_POWDER,
            "Pierce through the enemy you look at in a dash while damaging it",
            "the dash range is 5 blocks", RIGHT_CLICK),

    ESCAPE_ARTIST("Escape Artist", 45, new EscapeArtistAbility(), Material.RECOVERY_COMPASS,
            "Unleashes a powerful plus-shaped wave igniting enemies",
            "The wave's range is 10 blocks", DAMAGED),

    AXE_KICK("Axe Kick", 10, new AxeKickAbility(), Material.NETHERITE_AXE,
            "Land a critical hit the damage massively all nearby mobs",
            "The hit radius is 5 blocks and the damage is 1.5x", SHIFT, LEFT_CLICK),

    BLADE_DANCE("Blade Dance", 12, new BladeDance(), Material.MAGMA_CREAM,
            "Land a critical hit the damage massively all nearby mobs",
            "The hit radius is 5 blocks and the damage is 1.5x", Q),

    EXECUTION_FLOW("Execution Flow", 5, new ExecutionFlow(), Material.QUARTZ,
            "Land a critical hit the damage massively all nearby mobs",
            "The hit radius is 5 blocks and the damage is 1.5x", KILL);

    private final String displayName;
    private final double cooldown;
    private final Set<InputType> defaultInputTypes;
    private final AbilityBase abilityBase;
    private final Material icon;
    private final String damageDescription;
    private final String abilityDescription;

    AbilityType(String displayName, int cooldown, AbilityBase abilityBase, Material icon, String abilityDescription,
                String damageDescription, InputType... defaultInputTypes) {
        this.displayName = displayName;
        this.cooldown = cooldown;
        this.defaultInputTypes = Set.of(defaultInputTypes);
        this.abilityBase = abilityBase;
        this.icon = icon;
        this.abilityDescription = abilityDescription;
        this.damageDescription = damageDescription;
        // register the ability
        AtheriumPlugin.getInstance().registerListener(abilityBase);
    }

    public String getAbilityDescription() {
        return abilityDescription;
    }

    public String getDamageDescription() {
        return damageDescription;
    }

    public Material getIcon() {
        return icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getCooldown() {
        return cooldown;
    }

    public AbilityBase getAbilityBase() {
        return abilityBase;
    }

    public InputType[] getDefaultInputTypes() {
        return defaultInputTypes.toArray(new InputType[0]);
    }
}
