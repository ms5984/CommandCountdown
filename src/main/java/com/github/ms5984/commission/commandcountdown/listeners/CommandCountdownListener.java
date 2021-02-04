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
import com.github.ms5984.commission.commandcountdown.api.CommandCounter;
import com.github.ms5984.commission.commandcountdown.api.DefaultCounter;
import com.github.ms5984.commission.commandcountdown.api.PlayerCounter;
import com.github.ms5984.commission.commandcountdown.commands.CommandBase;
import com.github.ms5984.commission.commandcountdown.events.AsyncPlayerCommandBlockedEvent;
import com.github.ms5984.commission.commandcountdown.events.PlayerRunCommandEvent;
import com.github.ms5984.commission.commandcountdown.events.PlayerRunLimitedCommandEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Comparator;
import java.util.List;
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
        if (!api.hasCommandCounter(player, command) || player.hasPermission(CommandBase.Permissions.EXEMPT.permission)) {
            return;
        }
        final String[] eventArgs = e.getArgs();
        final boolean limitsCaseSensitive = api.limitsCaseSensitive();
        // Properly iterate over the new Set form
        final List<CommandCounter> counterList = api.getCommandCounters(player, command).parallelStream()
                .filter(cc -> {
                    final String[] ccArgs = cc.getArgs();
                    if (ccArgs.length == 0 && eventArgs.length == 0) return true;
                    if (eventArgs.length > ccArgs.length && !api.matchAllArgs()) {
                        return false;
                    }
                    int counter = 0;
                    for (String eArg : eventArgs) {
                        if (counter >= ccArgs.length) break;
                        if (!ccArgs[counter].matches("\\*+")) {
                            if (limitsCaseSensitive) {
                                if (!eArg.equals(ccArgs[counter])) return false;
                            } else {
                                if (!eArg.equalsIgnoreCase(ccArgs[counter])) return false;
                            }
                        }
                        counter++;
                    }
                    return true;
                }).sorted(Comparator.comparing(cc -> cc instanceof PlayerCounter)).collect(Collectors.toList());
        if (counterList.isEmpty()) return;
        if (counterList.parallelStream().noneMatch(cc -> cc.getLimit() != -1)) return;
        // We have figured out that this command is in fact limited
        e.setCancelled(true);
        plugin.getServer().getPluginManager().callEvent(new PlayerRunLimitedCommandEvent(e, counterList.toArray(new CommandCounter[0])));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerRunLimitedCommand(PlayerRunLimitedCommandEvent e) {
        for (CommandCounter commandCounter : e.getCommandCounters()) {
            if (commandCounter instanceof PlayerCounter) {
                final PlayerCounter counter = (PlayerCounter) commandCounter;
                if (counter.getLimit() > counter.getCurrentCount()) {
                    // current count is less than limit, event continues uncancelled
                    continue;
                }
                e.setCancelled(true);
            } else if (commandCounter instanceof DefaultCounter) {
                final DefaultCounter counter = (DefaultCounter) commandCounter;
                if (counter.getLimit() > counter.getCurrentCount(e.getPlayer())) {
                    // current count is less than limit, event continues uncancelled
                    continue;
                }
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRunCommandMonitor(PlayerRunLimitedCommandEvent e) {
        if (!e.isCancelled()) {
            final boolean success;
            if (api.keepCountOnFailure()) {
                String label = e.getOriginalCommandText();
                label = label.substring(label.indexOf("/") + 1, label.contains(" ") ? label.indexOf(" ") : label.length());
                success = e.getCommand().execute(e.getPlayer(), label, e.getArgs());
            } else {
                Bukkit.dispatchCommand(e.getPlayer(), e.getOriginalCommandText());
                success = true;
            }
            for (CommandCounter commandCounter : e.getCommandCounters()) {
                if (success) {
                    if (commandCounter instanceof PlayerCounter) {
                        final PlayerCounter playerCounter = (PlayerCounter) commandCounter;
                        playerCounter.increment();
                    } else if (commandCounter instanceof DefaultCounter) {
                        final DefaultCounter defaultCounter = (DefaultCounter) commandCounter;
                        defaultCounter.increment(e.getPlayer());
                    }
                }
            }
            return;
        }
        final AsyncPlayerCommandBlockedEvent asyncPlayerCommandBlockedEvent = new AsyncPlayerCommandBlockedEvent(e);
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getServer().getPluginManager().callEvent(asyncPlayerCommandBlockedEvent);
            }
        }.runTaskAsynchronously(plugin);
    }

    @EventHandler
    public void onPlayerRunCommandMonitor(AsyncPlayerCommandBlockedEvent e) {
        // send you have reached the use limit
        e.getPlayer().sendMessage(Messages.REACHED_LIMIT.toString());
        System.out.printf("Command '%s' blocked for %s\n", e.getOriginalCommandText(), e.getPlayer().getName());
    }
}
