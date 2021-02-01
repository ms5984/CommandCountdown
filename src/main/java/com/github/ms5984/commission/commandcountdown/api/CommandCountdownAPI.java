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

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public interface CommandCountdownAPI {

    /**
     * Check if a player has any CommandCounters for a given command
     * @param player the player to check
     * @param command the command to test
     * @return true if any found, false otherwise
     */
    boolean hasCommandCounter(Player player, Command command);

    /**
     * Get a new PlayerCounter for a given command.
     * @param command the command to count
     */
    PlayerCounter getNewPlayerCounter(Command command);

    /**
     * Get a new DefaultCounter for a given command.
     * @param command the command to count
     */
    DefaultCounter getNewDefaultCounter(Command command);

    /**
     * Obtain a read-only Set of CommandCounters for a given command and player.
     * @param player the player to limit
     * @param command the command being counted
     * @return CommandCounters for command and player
     */
    Set<CommandCounter> getCommandCounters(Player player, Command command);

    /**
     * Obtain full Set of all command counters for a given player.
     * @param player the player
     * @return set of all CommandCounters for the player
     */
    Set<CommandCounter> getCountedCommands(Player player);

    /**
     * Get a read-only Set of all default CommandCounters
     * @return default Set of CommandCounters
     */
    Set<DefaultCounter> getDefaults();

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
     * Lookup a Command by its String representation, matching all aliases.
     * <p>This implementation allows for persistent resolution back to
     * a Command across restarts and even between plugins.</p>
     * @param toString the toString of a command to retrieve
     * @return an Optional potentially containing the matching Command
     */
    Optional<Command> getCommandById(String toString);

    /**
     * Are limited commands' arguments case sensitive?
     * @return false, true by config flag
     */
    boolean limitsCaseSensitive();

    /**
     * Should we manually process commands for players and keep original count on failures?
     * <p>This feature prevents counts from incrementing when a command fails for a player.
     * It uses the boolean result of {@link Command#execute}, which might not return false in
     * all types of "failure" (no permissions, etc). If you want EVERY potential command
     * execution to be counted against the player, set this to false in the config.</p>
     * @return defaults to true, false by config flag
     */
    boolean keepCountOnFailure();

    /**
     * Should limits with fewer args than given also match?
     * @return true, false by config flag
     */
    boolean matchAllArgs();

    /**
     * Get an instance of the API using Bukkit's RegisteredServiceProvider.
     * @return CommandCountdownAPI
     * @throws IllegalStateException if the plugin is not registered
     */
    static CommandCountdownAPI getInstance() throws IllegalStateException {
        final RegisteredServiceProvider<CommandCountdownAPI> rsp = Bukkit.getServicesManager()
                .getRegistration(CommandCountdownAPI.class);
        if (rsp == null) throw new IllegalStateException("CommandCountdown is not loaded.");
        return rsp.getProvider();
    }
}
