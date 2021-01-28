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
package com.github.ms5984.commission.commandcountdown.api;

import com.github.ms5984.commission.commandcountdown.CommandCountdown;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public interface CommandCountdownAPI {

    /**
     * Check if a player has a CommandCounter for a given command
     * @param player the player to check
     * @param command the command to test
     * @return true if found, false otherwise
     */
    boolean hasCommandCounter(Player player, Command command);

    /**
     * Get the CommandCounter for a given command and player.
     * <p>If it does not yet exist, it will be created.</p>
     * @param command the command instance you seek to limit
     * @param player the player to limit
     * @return CommandCounter for player
     */
    CommandCounter getCommandCounter(Command command, Player player);

    /**
     * Retrieve a List of all commands limited for a given player.
     * @param player a player
     * @return a List of all CommandCounters
     */
    List<CommandCounter> getCountedCommands(Player player);

    /**
     * Retrieve a Set of all command labels for the server
     * @return an unmodifiable Set backed by the server command map
     */
    Set<String> getServerCommandListing();

    /**
     * Resolve a command label or alias into a Command.
     * @param name the label or alias to test
     * @return command if found or null
     */
    @Nullable
    Command getCommandByName(String name);

    /**
     * Lookup a Command by its hashcode, matching all aliases.
     * @param hashCode the hashcode of command to retrieve
     * @return command if found or null
     */
    @Nullable
    Command getCommandById(int hashCode);

    /**
     * Are limited commands' arguments case sensitive?
     * @return false, true by config flag
     */
    boolean limitsCaseSensitive();

    /**
     * Get an instance of the API.
     * @return CommandCountdownAPI
     */
    static CommandCountdownAPI getInstance() {
        return CommandCountdown.getAPI();
    }
}
