package org.atheriumPlugin.utility;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        item = new ItemStack(material);
        meta = item.getItemMeta();
    }

    public ItemStack build() {
        hideAllFlags();
        item.setItemMeta(meta);
        return item;
    }

    public ItemBuilder setCustomModelData(int num) {
        meta.setCustomModelData(num);
        return this;
    }

    public ItemBuilder setDisplayName(String name) {
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        meta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        meta.setLore(lore);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }

    public ItemBuilder setLoreWithName(String description, ChatColor color, int maxLineLength) {
        List<String> lore = toLore(description, color, maxLineLength);
        setDisplayName(lore.remove(0));
        setLore(lore);
        return this;
    }

    public ItemBuilder hideFlags(ItemFlag itemFlag) {
        meta.addItemFlags(itemFlag);
        return this;
    }

    // fixes attack speed
    public ItemBuilder fixCustomAttackSpeed() {
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(), "",
                0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        return this;
    }

    public ItemBuilder hideAllFlags() {
        for (ItemFlag flag : ItemFlag.values()) {
            meta.addItemFlags(flag);
        }
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder setOwner(Player player) {
        if (!(meta instanceof SkullMeta)) return this;
        SkullMeta skull = (SkullMeta) meta;
        skull.setOwningPlayer(player);
        item.setItemMeta(skull);
        return this;
    }

    public ItemBuilder setEnchantmentGlint(boolean set) {
        if (set) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.RIPTIDE, 1, false);
        } else {
            meta.removeEnchant(Enchantment.CHANNELING);
        }
        return this;
    }

    public static List<String> toLore(String description, ChatColor color, int maxLineLength) {
        String[] words = description.split(" ");
        StringBuilder currentLine = new StringBuilder();
        List<String> result = new ArrayList<>();

        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > maxLineLength) {
                result.add(color + currentLine.toString());
                currentLine = new StringBuilder();
            }

            if (!currentLine.isEmpty()) currentLine.append(" ");
            currentLine.append(word);
        }

        if (!currentLine.isEmpty()) result.add(color + currentLine.toString());

        return result;
    }

    public static ItemStack quick(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            item.setItemMeta(meta);
        }
        return item;
    }
}
