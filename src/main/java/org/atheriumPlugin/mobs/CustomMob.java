package org.atheriumPlugin.mobs;

import org.atheriumPlugin.stats.CustomStat;
import org.atheriumPlugin.stats.EntityStats;
import org.atheriumPlugin.stats.StatSystem;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class CustomMob {

    private final EntityStats mobStats;
    private final LivingEntity entity;

    public CustomMob(LivingEntity entity) {
        this.entity = entity;
        this.mobStats = StatSystem.getInstance().getStats(entity);
    }

    public CustomMob setStat(CustomStat stat, double value) {
        mobStats.setBaseStat(stat, value);
        return this;
    }

    public CustomMob setItem(ItemStack itemStack, EquipmentSlot equipmentSlot, int customId) {
        // if customId is 0, boom!!
        if (customId > 0) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setCustomModelData(customId);
            itemStack.setItemMeta(itemMeta);
        }
        // set drop chance to 0
        switch (equipmentSlot) {
            case HEAD -> Objects.requireNonNull(entity.getEquipment()).setHelmetDropChance(0.0f);
            case CHEST -> Objects.requireNonNull(entity.getEquipment()).setChestplateDropChance(0.0f);
            case LEGS -> Objects.requireNonNull(entity.getEquipment()).setLeggingsDropChance(0.0f);
            case FEET -> Objects.requireNonNull(entity.getEquipment()).setBootsDropChance(0.0f);
            case HAND -> Objects.requireNonNull(entity.getEquipment()).setItemInMainHandDropChance(0.0f);
            case OFF_HAND -> Objects.requireNonNull(entity.getEquipment()).setItemInOffHandDropChance(0.0f);
        }
        Objects.requireNonNull(entity.getEquipment()).setItem(equipmentSlot, itemStack);
        return this;
    }

    public CustomMob setName(String name) {
        entity.setCustomNameVisible(true);
        entity.setCustomName(ChatColor.translateAlternateColorCodes('&', name));
        return this;
    }

    public EntityStats build() {
        // heal to max hp
        mobStats.addOneTimeModifier(CustomStat.HEALTH,
                mobStats.getStat(CustomStat.MAX_HEALTH), 0);

        return mobStats;
    }
}
