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
package com.github.ms5984.commission.commandcountdown.model;

import com.github.ms5984.commission.commandcountdown.api.CommandCounter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Returned when a limit is deserialized but its original command is not currently found
 */
public final class NullCommand extends Command {

    private final String lastFQN;

    public NullCommand(CommandCounter counter) {
        super(counter.getLabel());
        this.lastFQN = ((Counter) counter).lastFQN;
    }

    /**
     * Get the last FQN successfully calculated for this command.
     * @return cached last FQN
     */
    public String getLastFQN() {
        return lastFQN;
    }

    // Command overrides

    @Override
    public boolean setName(@NotNull String name) {
        return false;
    }

    @Override
    public void setPermission(@Nullable String permission) {
    }

    @Override
    public boolean testPermission(@NotNull CommandSender target) {
        target.sendMessage("This is not a valid command.");
        return false;
    }

    @Override
    public boolean testPermissionSilent(@NotNull CommandSender target) {
        return false;
    }

    @Override
    public boolean setLabel(@NotNull String name) {
        return false;
    }

    @Override
    public boolean isRegistered() {
        return false;
    }

    @Override
    public @NotNull Command setAliases(@NotNull List<String> aliases) {
        return this;
    }

    @Override
    public @NotNull Command setDescription(@NotNull String description) {
        return this;
    }

    @Override
    public @NotNull Command setPermissionMessage(@Nullable String permissionMessage) {
        return this;
    }

    @Override
    public @NotNull Command setUsage(@NotNull String usage) {
        return this;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        return false;
    }
}
