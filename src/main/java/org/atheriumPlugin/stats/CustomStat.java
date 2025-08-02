package org.atheriumPlugin.stats;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Objects;

public enum CustomStat {

    MAX_HEALTH {
        @Override
        public void set(LivingEntity entity, double value) {
            if (value < 0) return;
            Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                    .setBaseValue(value);
        }

        @Override
        public String getDisplayName() {
            return getColor() + getIcon() +
                    ChatColor.WHITE + " Max Health";
        }

        @Override
        public ChatColor getColor() {
            return ChatColor.RED;
        }

        @Override
        public String getIcon() {
            return "❤";
        }
    },

    DEFENCE {
        @Override
        public void set(LivingEntity entity, double value) {
        }

        @Override
        public String getDisplayName() {
            return getColor() + getIcon() +
                    ChatColor.WHITE + " Defence";
        }

        @Override
        public ChatColor getColor() {
            return ChatColor.GREEN;
        }

        @Override
        public String getIcon() {
            return "@";
        }
    },

    HEALTH {
        @Override
        public void set(LivingEntity entity, double value) {
            if (value < 0) value = 0;
            value = Math.min(value, Objects.requireNonNull(
                    entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
            entity.setHealth(value);
        }

        @Override
        public String getDisplayName() {
            return "Current Health";
        }

        @Override
        public ChatColor getColor() {
            return ChatColor.RED;
        }

        @Override
        public String getIcon() {
            return "❤";
        }
    },

    SPEED {
        @Override
        public void set(LivingEntity entity, double value) {
            if (value < 0) value = 0;
            value = Math.min(value / 125, 1 - Double.MIN_VALUE);
            Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                    .setBaseValue(value);
        }

        @Override
        public String getDisplayName() {
            return getColor() +
                    getIcon() + ChatColor.WHITE + " Speed";
        }

        @Override
        public ChatColor getColor() {
            return ChatColor.DARK_AQUA;
        }

        @Override
        public String getIcon() {
            return "☄";
        }
    },

    DAMAGE {
        @Override
        public void set(LivingEntity entity, double value) {
        }

        @Override
        public String getDisplayName() {
            return getColor() +
                    getIcon() + ChatColor.WHITE + " Damage";
        }

        @Override
        public ChatColor getColor() {
            return ChatColor.GOLD;
        }

        @Override
        public String getIcon() {
            return "\uD83D\uDDE1";
        }
    },

    ATTACK_SPEED {
        public void set(LivingEntity entity, double value) {
            if (!(entity instanceof Player)) return; // mobs don't have it
            if (value < 0) value = 0;
            if (entity.getAttribute(Attribute.GENERIC_ATTACK_SPEED) != null) {
                Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_ATTACK_SPEED))
                        .setBaseValue(value / 2.5);
            }
        }

        @Override
        public String getDisplayName() {
            return getColor() +
                    getIcon() + ChatColor.WHITE + " Attack Speed";
        }

        @Override
        public ChatColor getColor() {
            return ChatColor.YELLOW;
        }

        @Override
        public String getIcon() {
            return "⚔";
        }
    };

    public abstract void set(LivingEntity entity, double value);

    public abstract String getDisplayName();

    public abstract ChatColor getColor();

    public abstract String getIcon();
}