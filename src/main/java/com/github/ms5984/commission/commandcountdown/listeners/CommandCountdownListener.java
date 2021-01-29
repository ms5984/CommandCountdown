/*
 *  Copyright 2021 ms5984 (Matt) <https://github.com/ms5984>
 *
 *  This file is part of CommandCountdown.
 *
 *  CommandCountdown is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  CommandCountdown is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.ms5984.commission.commandcountdown.listeners;

import com.github.ms5984.commission.commandcountdown.CommandCountdown;
import com.github.ms5984.commission.commandcountdown.Messages;
import com.github.ms5984.commission.commandcountdown.api.CommandCountdownAPI;
import com.github.ms5984.commission.commandcountdown.events.AsyncPlayerCommandBlockedEvent;
import com.github.ms5984.commission.commandcountdown.events.PlayerRunCommandEvent;
import com.github.ms5984.commission.commandcountdown.events.PlayerRunLimitedCommandEvent;
import com.github.ms5984.commission.commandcountdown.model.Counter;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CommandCountdownListener implements Listener {
    private final CommandCountdownAPI api;
    private final CommandCountdown plugin;

    public CommandCountdownListener(CommandCountdown plugin) {
        api = CommandCountdown.getAPI();
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerRunCommand(PlayerRunCommandEvent e) {
        final Player player = e.getPlayer();
        final Command command = e.getCommand();
        if (!api.hasCommandCounter(player, command)) {
            return;
        }
        final String[] eventArgs = e.getArgs();
        // Properly iterate over the new Set form
        outer : for (Counter counter : api.getCommandCounters(player, command)
                .parallelStream()
                .map(cc -> (Counter)cc)
                .collect(Collectors.toSet())) {
            final String[] counterArgs = counter.getArgs();
            if (api.limitsCaseSensitive()) {
                if (!Arrays.equals(eventArgs, counterArgs)) {
                    if (counterArgs.length <= eventArgs.length) {
                        if (counterArgs.length == 0) continue;
                        for (int i = 0; i < counterArgs.length; ++i) {
                            if (counterArgs[i].equals("*")) continue;
                            if (!eventArgs[i].equals(counterArgs[i])) {
                                continue outer;
                            }
                        }
                    } else continue;
                }
            } else {
                if (!Arrays.equals(eventArgs, counterArgs)) {
                    if (counterArgs.length <= eventArgs.length) {
                        if (counterArgs.length == 0) return;
                        for (int i = 0; i < counterArgs.length; ++i) {
                            if (counterArgs[i].equals("*")) continue;
                            if (!eventArgs[i].equalsIgnoreCase(counterArgs[i])) {
                                continue outer;
                            }
                        }
                    } else continue;
                }
            }
            if (counter.getLimit() == -1) continue;
            // We have figured out that this command is in fact limited
            e.setCancelled(true);
            plugin.getServer().getPluginManager().callEvent(new PlayerRunLimitedCommandEvent(e, counter));
            break;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerRunLimitedCommand(PlayerRunLimitedCommandEvent e) {
        final Counter counter = (Counter) e.getCommandCounter();
        if (counter.getLimit() > counter.getCurrentCount()) {
            // current count is less than limit, event continues uncancelled
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRunCommandMonitor(PlayerRunLimitedCommandEvent e) {
        if (!e.isCancelled()) {
            if (e.getCommand().execute(e.getPlayer(), e.getOriginalCommandText(), e.getArgs())) {
                ++((Counter) e.getCommandCounter()).count; // increment count on successful execute
            }
            // command failed, do not decrement count
        } else {
            final AsyncPlayerCommandBlockedEvent asyncPlayerCommandBlockedEvent = new AsyncPlayerCommandBlockedEvent(e);
            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getServer().getPluginManager().callEvent(asyncPlayerCommandBlockedEvent);
                }
            }.runTaskAsynchronously(plugin);
        }
    }

    @EventHandler
    public void onPlayerRunCommandMonitor(AsyncPlayerCommandBlockedEvent e) {
        // send you have reached the use limit
        e.getPlayer().sendMessage(Messages.REACHED_LIMIT.toString());
        System.out.printf("Command '%s' blocked for %s\n", e.getOriginalCommandText(), e.getPlayer().getName());
    }
}
