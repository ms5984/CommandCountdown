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
package com.github.ms5984.commission.commandcountdown.commands;

import com.github.ms5984.commission.commandcountdown.CommandCountdown;
import com.github.ms5984.commission.commandcountdown.Messages;
import com.github.ms5984.commission.commandcountdown.api.CommandCountdownAPI;
import com.github.ms5984.commission.commandcountdown.api.CommandCounter;
import com.github.ms5984.commission.commandcountdown.model.PlayerData;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CommandCountdownCommand extends CommandBase {
    private final CommandCountdownAPI api;

    public CommandCountdownCommand() {
        super(CommandData.COMMAND_COUNTDOWN);
        this.api = CommandCountdown.getAPI();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (args.length == 0) {
            // help+usage
        } else {
            if ("info".equalsIgnoreCase(args[0])) {// check permissions, check for player
                if (!sender.hasPermission(Permissions.INFO.permission)) {
                    sendMessage(sender, getPermissionMessage());
                    return true;
                }
                if (args.length == 1) {
                    if (!(sender instanceof Player)) {
                        sendMessage(sender, Messages.NOT_PLAYER);
                        return true;
                    }
                    // We have a player
                    final Player player = (Player) sender;
                    final Map<Command, CommandCounter> countedCommands = api.getCountedCommands(player);
                    if (countedCommands.isEmpty()) {
                        // You do not have any limited commands
                        sendMessage(sender, Messages.NO_LIMITS);
                        return true;
                    }
                    final List<String> counters = new ArrayList<>();
                    countedCommands.values().stream().map(Object::toString).forEach(counters::add);
                    if (counters.isEmpty()) {
                        // You do not have any limited commands
                        sendMessage(sender, Messages.NO_LIMITS);
                        return true;
                    }
                    // Your limited commands are as follows
                    sendMessage(sender, Messages.LIMIT_DATA);
                    sender.sendMessage(counters.toArray(new String[0]));
                    return true;
                } else if (args.length == 2) {
                    final Optional<Player> optionalPlayer = optionalOnlinePlayer(args[1]);
                    if (!optionalPlayer.isPresent()) {
                        // Is not the name of an online player
                        sendMessage(sender, Messages.PLAYER_NOT_FOUND);
                        return true;
                    }
                    // We have a player
                    final Player target = optionalPlayer.get();
                    final Map<Command, CommandCounter> countedCommands = api.getCountedCommands(target);
                    if (countedCommands.isEmpty()) {
                        // Target does not have any limited commands
                    }
                    // Target's limited commands
                    countedCommands.forEach((command, commandCounter) -> sendMessage(sender, commandCounter.toString()));
                    return true;
                }
            } else if ("reload".equalsIgnoreCase(args[0])) {// check permission and run reload
                if (!sender.hasPermission(Permissions.RELOAD.permission)) {
                    sendMessage(sender, getPermissionMessage());
                    return true;
                }
                Messages.initialize();
                sendMessage(sender, Messages.RELOAD);
                return true;
            } else if ("reset".equalsIgnoreCase(args[0])) {// check permission
                if (!sender.hasPermission(Permissions.RESET_PLAYER.permission)) {
                    sendMessage(sender, getPermissionMessage());
                    return true;
                }
                if (args.length == 1) {
                    sendMessage(sender, Messages.SPECIFY_PLAYER);
                    return true;
                }
                final Optional<Player> optionalPlayer = optionalOnlinePlayer(args[1]);
                if (!optionalPlayer.isPresent()) {
                    // Is not the name of an online player
                    sendMessage(sender, Messages.PLAYER_NOT_FOUND);
                    return true;
                }
                final Player target = optionalPlayer.get();
                if (args.length == 3) {
                    // checked if args[1] is name of player, is args[2] a command?
                    final Command commandByName = api.getCommandByName(args[2]);
                    if (commandByName == null) {
                        // message invalid command
                        sendMessage(sender, Messages.RESET_USAGE);
                        return true;
                    }
                    if (CommandCountdown.getAPI().hasCommandCounter(target, commandByName)) {
                        final PlayerData forPlayer = PlayerData.getForPlayer(target);
                        forPlayer.getPlayerLimits().remove(commandByName);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                forPlayer.saveToPdc();
                                sendMessage(sender, String.format(Messages.REMOVED_COMMAND.toString(), commandByName.toString(), target.getName()));
                            }
                        }.runTask(providingPlugin);
                        return true;
                    }
                    sendMessage(sender, Messages.NO_RESET);
                    return true;
                }
                if (args.length == 2) {
                    final PlayerData playerData = PlayerData.getForPlayer(target);
                    playerData.getPlayerLimits().clear();
                    playerData.clearPdc();
                    sendMessage(sender, String.format(Messages.CLEARED_PLAYER.toString(), target.getName()));
                    return true;
                }
            } else if ("setlimit".equalsIgnoreCase(args[0])) {// check permission, get int, get command, get args
                if (!sender.hasPermission(Permissions.SET_LIMIT.permission)) {
                    sendMessage(sender, getPermissionMessage());
                    return true;
                }
                if (args.length < 2) {
                    // msg usage (need int)
                    sendMessage(sender, Messages.NEED_INT);
                    return true;
                }
                final int limit;
                try {
                    limit = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    // msg invalid number
                    sendMessage(sender, String.format(Messages.INVALID_NUMBER.toString(), args[1]));
                    return true;
                }
                if (limit < -1) {
                    // avoid garbage objects
                    sendMessage(sender, String.format(Messages.INVALID_NUMBER.toString(), limit));
                    return true;
                }
                if (args.length < 3) {
                    sendMessage(sender, Messages.SPECIFY_PLAYER);
                    return true;
                }
                final Optional<Player> optionalPlayer = optionalOnlinePlayer(args[2]);
                if (!optionalPlayer.isPresent()) {
                    // Is not the name of an online player
                    sendMessage(sender, Messages.PLAYER_NOT_FOUND);
                    return true;
                }
                if (args.length < 4) {
                    // need to specify a command
                    sendMessage(sender, Messages.SPECIFY_COMMAND);
                    return true;
                }
                final Command commandByName = api.getCommandByName(args[3]);
                if (commandByName == null) {
                    sendMessage(sender, String.format(Messages.INVALID_COMMAND.toString(), args[3]));
                    return true;
                }
                final Player player = optionalPlayer.get();
                final CommandCounter commandCounter = api.getCommandCounter(commandByName, player);
                commandCounter.setLimit(limit);
                if (args.length > 4) {
                    commandCounter.setArgs(Arrays.copyOfRange(args, 4, args.length));
                }
                PlayerData.getForPlayer(player).putCommandCounter(commandCounter);
                System.out.println(commandCounter);
                return true;
            }
        }
        System.out.println("yeah flags");
        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        final List<String> completions = new ArrayList<>();
        final int length = args.length;
        if (length <= 1) {
            final List<String> tab0 = new ArrayList<>();
            if (sender.hasPermission(Permissions.INFO.permission)) {
                tab0.add("info");
            }
            if (sender.hasPermission(Permissions.SET_DEFAULT_LIMIT.permission)) {
                tab0.add("setdefaultlimit");
            }
            if (sender.hasPermission(Permissions.SET_LIMIT.permission)) {
                tab0.add("setlimit");
            }
            if (sender.hasPermission(Permissions.RELOAD.permission)) {
                tab0.add("reload");
            }
            if (sender.hasPermission(Permissions.RESET_PLAYER.permission)) {
                tab0.add("reset");
            }
            StringUtil.copyPartialMatches(args[0], tab0, completions);
        } else if (length == 2) {
            switch (args[0].toLowerCase()) {
                case "info":
                    if (sender.hasPermission(Permissions.INFO_OTHERS.permission)) {
                        return super.tabComplete(sender, alias, args);
                    }
                    break;
                case "reset":
                    if (sender.hasPermission(Permissions.RESET_PLAYER.permission)) {
                        return super.tabComplete(sender, alias, args);
                    }
                    break;
                case "setdefaultlimit":
                    if (!sender.hasPermission(Permissions.SET_DEFAULT_LIMIT.permission)) {
                        break;
                    }
                    return Arrays.asList("1", "2", "3");
                case "setlimit":
                    if (!sender.hasPermission(Permissions.SET_LIMIT.permission)) {
                        break;
                    }
                    return Arrays.asList("1", "2", "3");
            }
            return Collections.emptyList();
        } else if (length == 3) {
            if (args[0].equalsIgnoreCase("setdefaultlimit")) {
                StringUtil.copyPartialMatches(args[2], api.getServerCommandListing(), completions);
            } else if (args[0].equalsIgnoreCase("setlimit")) {
                return super.tabComplete(sender, alias, args);
            } else if (args[0].equalsIgnoreCase("reset")) {
                // produce list of limited labels for player
                final Optional<Player> playerOptional = optionalOnlinePlayer(args[1]);
                if (playerOptional.isPresent()) {
                    final Player player = playerOptional.get();
                    StringUtil.copyPartialMatches(args[2], PlayerData.getForPlayer(player)
                            .getPlayerLimits().keySet().stream().map(Command::getLabel)
                            .collect(Collectors.toCollection(ArrayList::new)), completions);
                }
            }
        } else if (length == 4) {
            if (args[0].equalsIgnoreCase("setlimit")) {
                StringUtil.copyPartialMatches(args[3], api.getServerCommandListing(), completions);
            } else if (args[0].equalsIgnoreCase("setdefaultlimit")) {
                if (api.getServerCommandListing().contains(args[2].toLowerCase())) {
                    final Command commandByName = api.getCommandByName(args[2]);
                    if (commandByName != null) {
                        return commandByName.tabComplete(sender, alias, Arrays.copyOfRange(args, 3, args.length));
                    }
                }
            }
        } else if (length == 5) {
            if (args[0].equalsIgnoreCase("setlimit")) {
                if (api.getServerCommandListing().contains(args[3].toLowerCase())) {
                    final Command commandByName = api.getCommandByName(args[3]);
                    if (commandByName != null) {
                        return commandByName.tabComplete(sender, alias, Arrays.copyOfRange(args, 4, args.length));
                    }
                }
            }
        }
        Collections.sort(completions);
        return completions;
    }

    private Optional<Player> optionalOnlinePlayer(String name) {
        final List<Player> players = ImmutableList.copyOf(Bukkit.getOnlinePlayers());
        return CompletableFuture.supplyAsync(() -> players.stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst()).join();
    }
}
