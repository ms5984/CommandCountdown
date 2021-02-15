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
import com.github.ms5984.commission.commandcountdown.api.DefaultCounter;
import com.github.ms5984.commission.commandcountdown.api.PlayerCounter;
import com.github.ms5984.commission.commandcountdown.commands.CommandBase;
import com.github.ms5984.commission.commandcountdown.commands.CommandCountdownCommand;
import com.github.ms5984.commission.commandcountdown.listeners.BukkitEventListener;
import com.github.ms5984.commission.commandcountdown.listeners.CommandCountdownListener;
import com.github.ms5984.commission.commandcountdown.model.*;
import lombok.val;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public final class CommandCountdown extends JavaPlugin implements CommandCountdownAPI {

    private static CommandCountdown instance;
    private Map<String, Command> commandMappings = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();
        saveResource("example-rule.yml", false);
        getServer().getServicesManager().register(CommandCountdownAPI.class, this, this, ServicePriority.Normal);
        loadCommandsMap();
        initializeFileAccess();
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
        getServer().getServicesManager().unregister(this);
    }

    private void loadCommandsMap() {
        try {
            val commandMapField = getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            val commandMap = (CommandMap) commandMapField.get(getServer());
            val knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            //noinspection unchecked
            commandMappings = ((Map<String, Command>) knownCommandsField.get(commandMap));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void initializeFileAccess() {
        YamlFile.initialize();
        Messages.initialize();
    }

    @Override
    public boolean hasCommandCounter(Player player, Command command) {
        if (command == null) return false;
        val hashCode = command.hashCode();
        return PlayerData.getForPlayer(player).getPlayerLimits().parallelStream()
                .map(CommandCounter::getBaseCommand)
                .map(Object::hashCode)
                .anyMatch(hc -> hc == hashCode);
    }

    @Override
    public PlayerCounter getNewPlayerCounter(Command command) {
        return new PlayerCounterImpl(command);
    }

    @Override
    public DefaultCounter getNewDefaultCounter(Command command) {
        return new DefaultCounterImpl(command);
    }

    @Override
    public Collection<CommandCounter> getCommandCounters(Player player, Command command) {
        if (command == null) return Collections.emptyList();
        val hashCode = command.hashCode();
        return Collections.unmodifiableList(getCountedCommands(player).parallelStream()
                .filter(cc -> cc.getBaseCommand().hashCode() == hashCode)
                .collect(Collectors.toList()));
    }

    @Override
    public Collection<CommandCounter> getCountedCommands(Player player) {
        final List<CommandCounter> fullList = new ArrayList<>();
        fullList.addAll(PlayerData.getForPlayer(player).getPlayerLimits());
        fullList.addAll(getDefaults());
        return Collections.unmodifiableList(fullList);
    }

    @Override
    public Set<DefaultCounter> getDefaults() {
        return Collections.emptySet();
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
    public Optional<Command> getCommandById(String toString) {
        val substring = toString.substring(0, toString.indexOf(")"));
        return commandMappings.values().parallelStream()
                .filter(c -> c.toString().startsWith(substring))
                .findAny();
    }

    @Override
    public boolean limitsCaseSensitive() {
        return getConfig().getBoolean("args-case-sensitive");
    }

    @Override
    public boolean keepCountOnFailure() {
        return getConfig().getBoolean("keep-count-on-failure");
    }

    @Override
    public boolean matchAllArgs() {
        return getConfig().getBoolean("match-all-args");
    }

    public static CommandCountdownAPI getAPI() {
        return instance;
    }
}
