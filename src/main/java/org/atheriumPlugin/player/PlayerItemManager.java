package org.atheriumPlugin.player;

import org.atheriumPlugin.items.CustomItem;
import org.atheriumPlugin.items.CustomItemRegistry;
import org.atheriumPlugin.utility.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.HashMap;

public class PlayerItemManager {

    private final Player player;
    private int itemHash;
    private final HashMap<EquipmentSlot, CustomItem> itemsMap = new HashMap<>();

    public PlayerItemManager(Player player) {
        this.player = player;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            itemsMap.put(slot, null);
        }
    }

    public CustomItem getItem(EquipmentSlot slot) {
        return itemsMap.get(slot);
    }

    public Collection<CustomItem> getItems() {
        return itemsMap.values();
    }

    // order-sensitive item hash
    private int getItemHash() {
        int hash = 1;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            int slotHash = 31 * slot.name().hashCode();

            ItemStack itemStack = player.getInventory().getItem(slot);
            if (itemStack != null) {
                slotHash = 31 * slotHash + itemStack.getType().toString().hashCode();

                ItemMeta meta = itemStack.getItemMeta();
                if (meta != null && meta.hasDisplayName()) {
                    slotHash = 31 * slotHash + meta.getDisplayName().hashCode()
                            + meta.getCustomModelData();
                }
            }

            hash = 31 * hash + slotHash;
        }

        return hash;
    }

    public boolean isNewHash() {
        int newHash = getItemHash();
        int oldHash = itemHash;
        itemHash = newHash;
        return oldHash != newHash;
    }

    public void reloadItems() {
        itemsMap.forEach((slot, curItem) -> {
            ItemStack itemStack = player.getInventory().getItem(slot);
            CustomItem customItem = (itemStack != null && itemStack.hasItemMeta())
                    ? CustomItemRegistry.getItem(getItemId(itemStack)) : null;
            if (customItem != null && !customItem.itemType().getSlot().equals(slot)) customItem = null;
            itemsMap.put(slot, customItem);
        });
    }

    private int getItemId(ItemStack itemStack) {
        return itemStack.getItemMeta() != null && itemStack.getItemMeta().hasCustomModelData()
                ? itemStack.getItemMeta().getCustomModelData() : 0;
    }
}
