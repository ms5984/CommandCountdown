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
package com.github.ms5984.commission.commandcountdown.events;

import com.github.ms5984.commission.commandcountdown.api.CommandCounter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Fired when a command is determined to be limited for a player.
 */
public final class PlayerRunLimitedCommandEvent extends PlayerRunCommandEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private final List<CommandCounter> counter;

    public PlayerRunLimitedCommandEvent(PlayerRunCommandEvent e, CommandCounter... counter) {
        super(e);
        cancelled = false;
        this.counter = Arrays.asList(counter);
    }

    /**
     * Get the CommandCounters for this player-command combo.
     * @return data for the player and command
     */
    public List<CommandCounter> getCommandCounters() {
        return Collections.unmodifiableList(counter);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
