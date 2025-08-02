package org.atheriumPlugin.player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.atheriumPlugin.AtheriumPlugin;
import org.atheriumPlugin.abilities.AbilityBase;
import org.atheriumPlugin.abilities.AbilityType;
import org.atheriumPlugin.abilities.InputType;
import org.atheriumPlugin.classes.ClassType;
import org.atheriumPlugin.items.ItemType;
import org.atheriumPlugin.utility.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PlayerAbility {

    private final AbilityType abilityData;
    private final AbilityBase ability;
    private final Player player;
    private final HashSet<InputType> inputTypes;
    private long lastExecuted;

    public PlayerAbility(AbilityType abilityData, UUID playerUUID, InputType... inputTypes) {
        this.abilityData = abilityData;
        this.player = Bukkit.getPlayer(playerUUID);
        this.ability = abilityData.getAbilityBase();
        this.inputTypes = new HashSet<>();
        this.inputTypes.addAll(List.of(inputTypes));
    }

    public void execute(Event event) {
        if (isOnCooldown() || !ability.canActivate(player, event) || !isHoldingClassItem(player)) {
            playFailureSound();
            return;
        }
        ability.execute(player, event);
        resetCooldown();
        displayCooldownMessage();
    }

    private boolean isOnCooldown() {
        return (double) (System.currentTimeMillis() - lastExecuted) / 1000 <= abilityData.getCooldown();
    }

    private void playFailureSound() {
        player.stopSound(Sound.BLOCK_CHAIN_FALL);
        player.playSound(player, Sound.BLOCK_ANVIL_FALL, 0.75F, 1F);
    }

    public void resetCooldown() {
        lastExecuted = System.currentTimeMillis();
    }

    public void setCooldownZero() {
        lastExecuted = 0;
    }

    public void reduceCooldown(double seconds) {
        lastExecuted -= (long) (seconds * 1000);
    }

    public static boolean isHoldingClassItem(Player player) {
        ClassType classType = PlayerSystem.getInstance().getAbilityManager(player).getClassType();
        if (classType.equals(ClassType.DEFAULT)) return false;
        ItemType classItemType = classType.getItemType();
        return InputType.isHolding(player, classItemType.getSlot(), classItemType);
    }

    private void displayCooldownMessage() {
        Bukkit.getScheduler().runTaskLater(AtheriumPlugin.getInstance(), () ->
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                        ChatColor.GOLD + (abilityData.getDisplayName() + " is off cooldown")
                )), (long) abilityData.getCooldown() * 20);
    }

    public AbilityType getAbilityType() {
        return abilityData;
    }

    public Set<InputType> getInputTypes() {
        return inputTypes;
    }
}
