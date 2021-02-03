/*
 *  Copyright 2021 ms5984 (Matt) <https://github.com/ms5984>
 *
 *  This file is part of CommandCountdown.
 *
 *  CommandCountdown is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  CommandCountdown is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.ms5984.commission.commandcountdown.util;

import com.github.ms5984.commission.commandcountdown.api.CommandCountdownAPI;
import com.github.ms5984.commission.commandcountdown.model.NullCommand;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A set of utilities for working with Commands.
 */
public class CommandUtil {
    private static CommandCountdownAPI api;

    /**
     * Get a sanitized version of {@link Command#toString()} that
     * strips out the version portion of {@link PluginCommand}'s
     * implementation.
     * @param command command to sanitize
     * @return toString of Command without version String
     */
    public static String getSanitized_toString(Command command) {
        if (command instanceof PluginCommand) {
            final StringBuilder sb = new StringBuilder(command.toString());
            sb.delete(sb.indexOf(" v"), sb.indexOf(")"));
            return sb.toString();
        }
        return command.toString();
    }

    /**
     * Get ALL labels for a command using data directly from
     * the server commandMap.
     * @param command the command to look for
     * @return a Set of command labels
     */
    public static Set<String> getLabels(Command command) {
        if (command instanceof NullCommand || command == null) return Collections.emptySet();
        final String label = command.getLabel();
        final String command_toString = command.toString();
        return getAPI().getServerCommandListing().parallelStream()
                .filter(s -> s.endsWith(":" + label))
                .filter(s -> {
                    final Command commandByName = getAPI().getCommandByName(s);
                    if (commandByName == null) return false;
                    return commandByName.toString().equals(command_toString);
                }).collect(Collectors.toSet());
    }

    /**
     * Get all fallback-prefixed labels using data directly from
     * the server commandMap.
     * @param command the command to look for
     * @return an Optional String
     */
    public static Set<String> getFallbackPrefixedLabel(Command command) {
        if (command instanceof NullCommand || command == null) return Collections.emptySet();
        return getLabels(command).parallelStream()
                .filter(s -> s.contains(":"))
                .collect(Collectors.toSet());
    }

    private static CommandCountdownAPI getAPI() {
        if (api == null) {
            api = CommandCountdownAPI.getInstance();
        }
        return api;
    }
}
