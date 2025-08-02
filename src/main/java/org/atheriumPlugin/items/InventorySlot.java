package org.atheriumPlugin.items;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public enum InventorySlot {

    MAIN_HAND {
        @Override
        public ItemStack getItem(PlayerInventory inventory) {
            return inventory.getItemInMainHand();
        }
    },

    OFF_HAND {
        @Override
        public ItemStack getItem(PlayerInventory inventory) {
            return inventory.getItemInOffHand();
        }
    },

    BOOTS {
        @Override
        public ItemStack getItem(PlayerInventory inventory) {
            return inventory.getBoots();
        }
    },

    LEGGINGS {
        @Override
        public ItemStack getItem(PlayerInventory inventory) {
            return inventory.getLeggings();
        }
    },

    CHESTPLATE {
        @Override
        public ItemStack getItem(PlayerInventory inventory) {
            return inventory.getChestplate();
        }
    },

    HELMET {
        @Override
        public ItemStack getItem(PlayerInventory inventory) {
            return inventory.getHelmet();
        }
    };

    public abstract ItemStack getItem(PlayerInventory inventory);
}
