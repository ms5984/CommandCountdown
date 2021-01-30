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
import com.github.ms5984.commission.commandcountdown.events.PlayerRunCommandEvent;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class BukkitEventListener implements Listener {
    private final Plugin plugin;

    public BukkitEventListener(CommandCountdown plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommandPreProcessEvent(PlayerCommandPreprocessEvent e) {
        final String[] split = e.getMessage().split(" +");
        final Command commandByName = CommandCountdown.getAPI().getCommandByName(split[0].substring(1));
        if (commandByName != null) {
            final PlayerRunCommandEvent event;
            if (split.length > 1) {
                event = new PlayerRunCommandEvent(e, commandByName, Arrays.copyOfRange(split, 1, split.length));
            } else {
                event = new PlayerRunCommandEvent(e, commandByName);
            }
            plugin.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                e.setCancelled(true);
            }
        }
    }
}
