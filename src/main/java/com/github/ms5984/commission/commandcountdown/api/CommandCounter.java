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

import org.bukkit.command.Command;

import java.io.Serializable;

/**
 * Describes a command+arg combination
 */
public interface CommandCounter extends Serializable {
    /**
     * Get an instance of the Command whose usage
     * is tracked and counted by this class.
     * @return corresponding Command object
     */
    Command getBaseCommand();

    /**
     * Get the label/plain name of this command.
     * @return name
     */
    String getLabel();

    /**
     * Get a more descriptive name for this command.
     * <p>This includes the origin system; "bukkit:"
     * for Bukkit commands, "PluginName:" for plugins.</p>
     * @return label prepended with namespace
     */
    String getFQN();

    /**
     * Get the arguments required for this counter.
     * <p>Array may have length = 0</p>
     * @return Array of String args
     */
    String[] getArgs();

    /**
     * Set the arguments required for this counter.
     * <p>Optional</p>
     * @param args any number of subsequent arguments
     */
    void setArgs(String... args);

    /**
     * Get current number of uses.
     * @return number of times the command was used since
     * last reset of current count.
     */
    int getCurrentCount();

    /**
     * Set current number of uses.
     * @param uses new number of uses
     */
    void setCurrentCount(int uses);

    /**
     * Get the current set limit.
     *
     * @return max number of uses. Returns -1 if unlimited
     */
    default int getLimit() {
        return -1;
    }

    /**
     * Set the count limit.
     * @param count number of uses
     */
    void setLimit(int count);

    /**
     * Resets the current number of uses.
     */
    void resetCurrentCount();
}
