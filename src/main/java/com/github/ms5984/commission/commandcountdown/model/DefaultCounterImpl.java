/*
 *  Copyright 2021 ms5984 (Matt) <https://github.com/ms5984>
 *
 *  This file is part of CommandCountdown.
 *
 *  CommandCountdown is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  CommandCountdown is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.ms5984.commission.commandcountdown.model;

import com.github.ms5984.commission.commandcountdown.api.DefaultCounter;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class DefaultCounterImpl extends AbstractCounter implements DefaultCounter {
    private static final long serialVersionUID = -5047686256799959507L;
    private static JavaPlugin providingPlugin;
    private final ConfigurationSection configurationSection;

    public DefaultCounterImpl(Command command) {
        super(command);
        if (providingPlugin == null) {
            providingPlugin = JavaPlugin.getProvidingPlugin(DefaultCounterImpl.class);
        }
        ConfigurationSection defaultLimits = providingPlugin.getConfig().getConfigurationSection("default-limits");
        if (defaultLimits == null) {
            defaultLimits = providingPlugin.getConfig().createSection("default-limits");
        }
        final ConfigurationSection toStringSection = defaultLimits.getConfigurationSection(command.toString());
        if (toStringSection == null) {
            final ConfigurationSection labelSection = defaultLimits.getConfigurationSection(label);
            if (labelSection == null) {
                final ConfigurationSection fqnSection = defaultLimits.getConfigurationSection(lastFQN.toLowerCase());
                if (fqnSection == null) {
                    // create new section using toString
                    this.configurationSection = defaultLimits.createSection(command.toString());
                } else {
                    this.configurationSection = fqnSection;
                }
            } else {
                this.configurationSection = labelSection;
            }
        } else {
            this.configurationSection = toStringSection;
        }
        this.configurationSection.set("args", args);
        this.configurationSection.set("limit", limit);
    }

    private ConfigurationSection getUsesSection() {
        final ConfigurationSection uses = configurationSection.getConfigurationSection("uses");
        if (uses == null) return configurationSection.createSection("uses");
        return uses;
    }

    @Override
    public int getCurrentCount(OfflinePlayer player) {
        return getUsesSection().getInt(player.getUniqueId().toString(), 0);
    }

    @Override
    public void setCurrentCount(OfflinePlayer player, int uses) {
        getUsesSection().set(player.getUniqueId().toString(), uses);
    }

    @Override
    public void increment(OfflinePlayer player) {
        setCurrentCount(player, getCurrentCount(player) + 1);
    }

    @Override
    public void resetCurrentCount(OfflinePlayer player) {
        getUsesSection().set(player.getUniqueId().toString(), null);
    }
}
