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
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Fired whenever a player sends a potentially limited command.
 */
public class PlayerRunCommandEvent extends CommandCountdownEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    protected final Player player;
    protected final Command command;
    protected final String[] args;
    protected final String originalCommandText;
    protected boolean cancelled;

    public PlayerRunCommandEvent(PlayerCommandPreprocessEvent e, @NotNull Command command, String... args) {
        this.player = e.getPlayer();
        this.command = command;
        this.args = args;
        this.originalCommandText = e.getMessage();
    }

    public PlayerRunCommandEvent(PlayerRunCommandEvent e) {
        this.player = e.player;
        this.command = e.command;
        this.args = e.args;
        this.originalCommandText = e.originalCommandText;
        this.cancelled = e.cancelled;
    }

    /**
     * Get the Player who is running the command.
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the Command being run.
     * @return the command
     */
    public Command getCommand() {
        return command;
    }

    /**
     * Get an array representing the command arguments.
     * @return String array, potentially of size 0.
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * Get original text String sent by the user.
     * @return the actual text sent by the user
     */
    public String getOriginalCommandText() {
        return originalCommandText;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
