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

import com.github.ms5984.commission.commandcountdown.CommandCountdown;
import com.github.ms5984.commission.commandcountdown.api.CommandCounter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Counter implements CommandCounter {

    private static final long serialVersionUID = 9016468129961710867L;
    private transient Command command;
    private final String label;
    private final List<String> args = new ArrayList<>();
    protected String lastFQN;
    public final String command_toString;
    public int count;
    private int limit;

    public Counter(Command command) {
        this.command = command;
        this.label = command.getLabel();
        this.command_toString = command.toString();
        this.count = 0;
        this.limit = -1;
        this.lastFQN = getFQN();
    }

    @Override
    public Command getBaseCommand() {
        if (command == null) {
            command = CommandCountdown.getAPI().getCommandById(command_toString).orElseGet(() -> new NullCommand(this));
        }
        return command;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getFQN() {
        try {
            final JavaPlugin providingPlugin = JavaPlugin.getProvidingPlugin(getBaseCommand().getClass());
            return providingPlugin.getName() + ":" + label;
        } catch (IllegalArgumentException e) {
            // search the commandMap
            return CommandCountdown.getAPI().getServerCommandListing().stream()
                    .filter(s -> s.endsWith(":" + label))
                    .filter(s -> {
                        final Command commandByName = CommandCountdown.getAPI().getCommandByName(s);
                        if (commandByName == null) return false;
                        return commandByName.toString().equals(command_toString);
                    }).findAny().orElse("?:" + label);
        }
    }

    @Override
    public String[] getArgs() {
        return args.toArray(new String[0]);
    }

    @Override
    public void setArgs(String... args) {
        this.args.clear();
        this.args.addAll(Arrays.asList(args));
    }

    @Override
    public int getCurrentCount() {
        return count;
    }

    @Override
    public void setCurrentCount(int uses) {
        this.count = uses;
    }

    @Override
    public int getLimit() {
        return limit;
    }

    @Override
    public void setLimit(int count) {
        this.limit = count;
    }

    @Override
    public void resetCurrentCount() {
        this.count = 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(command_toString, args);
    }

    @Override
    public String toString() {
        return ChatColor.translateAlternateColorCodes('&',
                String.format("&7Command: '&e%s&7' &8args:&7%s &elimit:[%s] &bcount:[%s]",
                        (command instanceof NullCommand) ? "missing!" + ((NullCommand) command).getLastFQN() : getFQN(),
                        (args.isEmpty()) ? "NONE" : args, limit, count));
    }
}
