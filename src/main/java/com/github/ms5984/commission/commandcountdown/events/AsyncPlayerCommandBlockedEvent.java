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

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired async following the cancellation of a PlayerCommandPreprocessEvent
 */
public final class AsyncPlayerCommandBlockedEvent extends CommandCountdownEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final Command command;
    private final String originalCommandText;

    public AsyncPlayerCommandBlockedEvent(PlayerRunCommandEvent e) {
        super(true);
        player = e.player;
        command = e.command;
        originalCommandText = e.originalCommandText;
    }

    /**
     * Get the Player who attempted to run the command.
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the Command that would be run.
     * @return the command
     */
    public Command getCommand() {
        return command;
    }

    /**
     * Get original text String sent by the user.
     * @return the actual text sent by the user
     */
    public String getOriginalCommandText() {
        return originalCommandText;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
