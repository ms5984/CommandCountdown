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

import org.bukkit.OfflinePlayer;

/**
 * Describes a global command+arg combination
 */
public interface DefaultCounter extends CommandCounter {
    /**
     * Get a player's current number of uses.
     * @return number of times the command was used since
     * last reset of current count.
     */
    int getCurrentCount(OfflinePlayer player);

    /**
     * Set a player's current number of uses.
     * @param uses new number of uses
     */
    void setCurrentCount(OfflinePlayer player, int uses);

    /**
     * Increment player's use count.
     */
    void increment(OfflinePlayer player);

    /**
     * Reset a player's current number of uses.
     */
    void resetCurrentCount(OfflinePlayer player);
}
