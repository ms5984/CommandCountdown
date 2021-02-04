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
package com.github.ms5984.commission.commandcountdown.model;

import com.github.ms5984.commission.commandcountdown.CommandCountdown;
import com.github.ms5984.commission.commandcountdown.api.DefaultCounter;
import com.github.ms5984.commission.commandcountdown.util.CommandUtil;
import org.bukkit.command.Command;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigCommandData {
    private static final Supplier<File> COMMAND_FOLDER = () -> {
        final File commandFolder = new File(JavaPlugin.getProvidingPlugin(ConfigCommandData.class).getDataFolder(), "commands");
        if (!commandFolder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            commandFolder.mkdir();
        }
        return commandFolder;
    };
    private static final Map<Command, ConfigCommandData> defaults = new HashMap<>();
    private final File file;
    private final YamlConfiguration configuration;
    private final ConfigurationSection limitsSection;
    private final Set<DefaultCounter> counters = new HashSet<>();

    private ConfigCommandData(Command command) {
        final Set<String> fallbackPrefixedLabel = CommandUtil.getFallbackPrefixedLabel(command);
        if (fallbackPrefixedLabel.isEmpty()) throw new IllegalArgumentException("Fallback-prefixed command not found!");
        final String fallbackName = fallbackPrefixedLabel.stream().findAny()
                .map(s -> s.replace(":", "-") + ".yml")
                .get();
        this.file = CommandUtil.getLabels(command).parallelStream()
                .map(s -> s.replace(":", "-") + ".yml")
                .map(s -> new File(COMMAND_FOLDER.get(), s))
                .filter(File::exists)
                .findAny().orElseGet(() -> new File(COMMAND_FOLDER.get(), fallbackName));
        if (file.exists()) {
            configuration = YamlConfiguration.loadConfiguration(file);
        } else {
            final File simpleLabel = new File(COMMAND_FOLDER.get(), command.getLabel());
            final YamlConfiguration newConfig = new YamlConfiguration();
            if (simpleLabel.exists()) {
                newConfig.addDefaults(YamlConfiguration.loadConfiguration(simpleLabel));
            }
            configuration = newConfig;
        }
        this.limitsSection = (configuration.isConfigurationSection("limits"))
                ? configuration.getConfigurationSection("limits") : configuration.createSection("limits");
    }

    public void save() {
        try {
            this.configuration.save(file);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save configuration to CommandCountdown/commands/" + file.getName(), e);
        }
    }

    public void storeDefault(DefaultCounter defaultCounter) {
        final ConfigurationSection counterSection = getCounterSection(defaultCounter)
                .orElseGet(() -> limitsSection.createSection(UUID.randomUUID().toString()));
        counterSection.set("args", defaultCounter.getArgs());
        counterSection.set("limit", defaultCounter.getLimit());
        save();
    }

    public ConfigurationSection getUsesSection(DefaultCounter defaultCounter) {
        return getCounterSection(defaultCounter).orElseGet(() -> {
            final ConfigurationSection counterSection = limitsSection.createSection(UUID.randomUUID().toString());
            counterSection.set("args", defaultCounter.getArgs());
            counterSection.set("limit", defaultCounter.getLimit());
            final ConfigurationSection uses = counterSection.createSection("uses");
            save();
            return uses;
        });
    }

    public Optional<ConfigurationSection> getCounterSection(DefaultCounter defaultCounter) {
        for (String key : limitsSection.getKeys(false)) {
            final ConfigurationSection keySection = limitsSection.getConfigurationSection(key);
            if (keySection != null) {
                if (Arrays.equals(keySection.getStringList("args").toArray(), defaultCounter.getArgs())) {
                    return Optional.of(keySection);
                }
            }
        }
        return Optional.empty();
    }

    public static ConfigCommandData getForCommand(Command command) {
        return defaults.computeIfAbsent(command, ConfigCommandData::new);
    }

    public static Set<DefaultCounter> getFiles() {
        final File commandFolder = new File(JavaPlugin.getProvidingPlugin(ConfigCommandData.class).getDataFolder(), "commands");
        //noinspection ResultOfMethodCallIgnored
        commandFolder.mkdir();
        final Set<String> commandLabels = CommandCountdown.getAPI().getServerCommandListing();
        return Arrays.stream(commandFolder.listFiles()).parallel()
                .filter(file -> file.getName().endsWith(".yml"))
                .flatMap(file -> {
                    final String name = file.getName().substring(0, file.getName().indexOf(".")).replace("-", ":");
                    if (commandLabels.contains(name)) {
                        final Command command = CommandCountdown.getAPI().getCommandByName(name);
                        if (command != null) {
                            final Set<DefaultCounter> counters = new HashSet<>();
                            final ConfigCommandData forCommand = getForCommand(command);
                            if (!forCommand.counters.isEmpty()) {
                                return forCommand.counters.stream();
                            }
                            for (String key : forCommand.configuration.getKeys(false)) {
                                final List<String> args = forCommand.configuration.getStringList(key + ".args");
                                final int limit = forCommand.configuration.getInt(key + ".limit", -1);
                                if (limit <= -1) continue;
                                final DefaultCounter counter = new DefaultCounterImpl(command);
                                counter.setLimit(limit);
                                counter.setArgs(args.toArray(new String[0]));
                                counters.add(counter);
                            }
                            forCommand.counters.addAll(counters);
                            return counters.stream();
                        }
                    }
                    return Stream.empty();
                }).collect(Collectors.toSet());
    }
}
