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
package com.github.ms5984.commission.commandcountdown;

import com.github.ms5984.commission.commandcountdown.api.CommandCountdownAPI;
import com.github.ms5984.commission.commandcountdown.api.CommandCounter;
import com.github.ms5984.commission.commandcountdown.commands.CommandBase;
import com.github.ms5984.commission.commandcountdown.commands.CommandCountdownCommand;
import com.github.ms5984.commission.commandcountdown.listeners.BukkitEventListener;
import com.github.ms5984.commission.commandcountdown.listeners.CommandCountdownListener;
import com.github.ms5984.commission.commandcountdown.model.Counter;
import com.github.ms5984.commission.commandcountdown.model.PlayerData;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public final class CommandCountdown extends JavaPlugin implements CommandCountdownAPI {

    private static CommandCountdown instance;
    private Map<String, Command> commandMappings = null;
    private NamespacedKey dataKey = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        loadCommandsMap();
        Messages.initialize();
        CommandBase.Permissions.registerPermissions();
        new BukkitEventListener(this);
        new CommandCountdownListener(this);
        new CommandCountdownCommand();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        PlayerData.saveData();
        commandMappings = null;
    }

    private void loadCommandsMap() {
        try {
            final Field commandMapField = getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            final CommandMap commandMap = (CommandMap) commandMapField.get(getServer());
            final Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            //noinspection unchecked
            commandMappings = ((Map<String, Command>) knownCommandsField.get(commandMap));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasCommandCounter(Player player, Command command) {
        if (command == null) return false;
        return PlayerData.getForPlayer(player).getPlayerLimits().keySet().stream()
                .map(Command::hashCode)
                .anyMatch(hc -> hc == command.hashCode());
    }

    @Override
    public CommandCounter getCommandCounter(Command command, Player player) {
        return PlayerData.getForPlayer(player).getPlayerLimits().entrySet().stream()
                .filter(entry -> entry.getKey().hashCode() == command.hashCode())
                .findAny()
                .map(Map.Entry::getValue)
                .orElseGet(() -> PlayerData.getForPlayer(player).getPlayerLimits().computeIfAbsent(command, Counter::new));
    }

    @Override
    public Map<Command, CommandCounter> getCountedCommands(Player player) {
        return Collections.unmodifiableMap(PlayerData.getForPlayer(player).getPlayerLimits());
    }

    @Override
    public Set<String> getServerCommandListing() {
        return Collections.unmodifiableSet(commandMappings.keySet());
    }

    @Override
    public @Nullable Command getCommandByName(String name) {
        return commandMappings.get(name);
    }

    @Override
    public @Nullable Command getCommandById(int hashCode) {
        return commandMappings.values().stream().filter(c -> hashCode() == hashCode).findAny().orElse(null);
    }

    @Override
    public boolean limitsCaseSensitive() {
        return getConfig().getBoolean("");
    }

    public static CommandCountdownAPI getAPI() {
        return instance;
    }

    public static NamespacedKey getDataKey() {
        if (instance.dataKey == null) {
            instance.dataKey = new NamespacedKey(instance, "command-data");
        }
        return instance.dataKey;
    }
}
