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

import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;

public class CommandUtil {
    public static String getSanitized_toString(Command command) {
        if (command instanceof PluginCommand) {
            final StringBuilder sb = new StringBuilder(command.toString());
            sb.delete(sb.indexOf(" v"), sb.indexOf(")"));
            return sb.toString();
        }
        return command.toString();
    }
}
