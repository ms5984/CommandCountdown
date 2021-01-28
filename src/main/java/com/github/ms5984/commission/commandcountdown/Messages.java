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
package com.github.ms5984.commission.commandcountdown;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public enum Messages {
    NO_PERMISSION("Messages.No-Permission"),
    NOT_PLAYER("Messages.Not-Player"),
    REACHED_LIMIT("Messages.Reached-Limit"),
    SPECIFY_PLAYER("Messages.Specify-Player"),
    PLAYER_NOT_FOUND("Messages.Player-Not-Found"),
    CLEARED_PLAYER("Messages.Cleared-Player"),
    RESET_USAGE("Messages.Reset-Usage"),
    NO_RESET("Messages.No-Reset"),
    REMOVED_COMMAND("Messages.Removed-Command"),
    RELOAD("Messages.Reload"),
    NEED_INT("Messages.Need-Int"),
    INVALID_NUMBER("Messages.Invalid-Num"),
    SPECIFY_COMMAND("Messages.Specify-Command"),
    INVALID_COMMAND("Messages.Invalid-Command"),
    NO_LIMITS("Messages.No-Limits"),
    LIMIT_DATA("Messages.Limit-Data");

    private static Configuration configuration;

    private final String confLine;

    Messages(String path) {
        this.confLine = path;
    }

    @Nullable
    public String get() {
        return configuration.getString(confLine);
    }

    @Override
    public String toString() {
        final String get = get();
        if (get == null) return "null";
        return ChatColor.translateAlternateColorCodes('&', get);
    }

    public static void initialize() {
        final JavaPlugin providingPlugin = JavaPlugin.getProvidingPlugin(Messages.class);
        final File file = new File(providingPlugin.getDataFolder(), "messages.yml");
        if (file.exists()) {
            configuration = YamlConfiguration.loadConfiguration(file);
        } else {
            configuration = new YamlConfiguration();
        }
        final InputStream resource = Objects.requireNonNull(providingPlugin.getResource("messages.yml"));
        configuration.addDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(resource)));
    }
}
