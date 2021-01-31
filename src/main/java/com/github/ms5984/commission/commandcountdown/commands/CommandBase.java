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
package com.github.ms5984.commission.commandcountdown.commands;

import com.github.ms5984.commission.commandcountdown.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public abstract class CommandBase extends Command {
    protected final Plugin providingPlugin = JavaPlugin.getProvidingPlugin(CommandBase.class);
    protected final CommandData commandData;

    protected CommandBase(CommandData commandData) {
        super(commandData.label);
        setDescription(commandData.description);
        setPermission(commandData.permissionNode);
        setPermissionMessage(Messages.NO_PERMISSION.get());
        setAliases(commandData.aliases);
        this.commandData = commandData;
        try {
            final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            final CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            commandMap.register(getLabel(), providingPlugin.getName(), this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected void sendMessage(CommandSender sender, String message) {
        if (message != null) sender.sendMessage(message);
    }

    protected void sendMessage(CommandSender sender, Messages message) {
        if (message != null) sender.sendMessage(message.toString());
    }

    protected enum CommandData {
        COMMAND_COUNTDOWN("commandcountdown", "Limit commands to a given count", "commandcountdown.limit", "cc", "cl");

        public final String label;
        public final String description;
        public final String permissionNode;
        public final List<String> aliases;

        CommandData(String label, String description, String permissionNode, String... aliases) {
            this.label = label;
            this.description = description;
            this.permissionNode = permissionNode;
            this.aliases = Arrays.asList(aliases);
        }
    }

    public enum Permissions {
        STAR(new Permission("cc.*", "All CommandCountdown permissions")),
        INFO_STAR(new Permission("cc.info.*", "Information of own limits and of others"), STAR, true),
        INFO_OTHERS(new Permission("cc.info.others", "Inspect limits of others"), INFO_STAR, true),
        INFO(new Permission("cc.info", "Information about your own limits", PermissionDefault.TRUE), INFO_STAR, true),
        RESET_DEFAULT_LIMIT(new Permission("cc.resetdefaultlimit", "Remove server-wide default limit"), STAR, true),
        SET_DEFAULT_LIMIT(new Permission("cc.setdefaultlimit", "Setup server-wide default limit"), STAR, true),
        SET_LIMIT(new Permission("cc.setlimit", "Set player-specific limits"), SET_DEFAULT_LIMIT, true),
        EXEMPT(new Permission("cc.exempt", "Temporarily bypass all limits", PermissionDefault.FALSE), STAR, false),
        RESET_PLAYER(new Permission("cc.resetplayer", "Reset a player's limits to the default"), STAR, true),
        RELOAD(new Permission("cc.reload", "Reload messages.yml and main configuration file"), STAR, true);

        public final String permissionNode;
        public final Permission permission;

        Permissions(Permission permission) {
            this.permission = permission;
            this.permissionNode = permission.getName();
        }

        Permissions(Permission permission, Permissions parent, boolean value) {
            this(permission);
            permission.addParent(parent.permission, value);
        }

        @Override
        public String toString() {
            return permissionNode;
        }

        public static void registerPermissions() {
            for (Permissions value : values()) {
                Bukkit.getServer().getPluginManager().addPermission(value.permission);
            }
        }
    }
}
