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

import lombok.var;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class YamlFile {
    private static JavaPlugin plugin;
    private static final List<YamlFile> FILES = new ArrayList<>();
    private final String[] directory;
    private final String filename;
    private final File file;
    private final YamlConfiguration configuration;

    private YamlFile(@NotNull String filename, String... path) {
        final File dir;
        if (Arrays.stream(path).parallel().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Path members cannot be null!");
        }
        this.directory = path;
        if (path.length > 0) {
            var finalDir = plugin.getDataFolder();
            for (String subDir : path) {
                finalDir = new File(finalDir, subDir);
                //noinspection ResultOfMethodCallIgnored
                finalDir.mkdir();
            }
            dir = finalDir;
        } else {
            dir = plugin.getDataFolder();
        }
        this.filename = filename;
        this.file = new File(dir, filename.endsWith(".yml") ? filename : filename.concat(".yml"));
        this.configuration = file.exists() ? YamlConfiguration.loadConfiguration(file) : new YamlConfiguration();
        FILES.add(this);
    }

    public YamlConfiguration getConfig() {
        return configuration;
    }

    public void delete() {
        //noinspection ResultOfMethodCallIgnored
        file.delete();
        try {
            if (file.createNewFile()) {
                configuration.load(file);
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public boolean exists() {
        return file.exists();
    }

    public void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save config!");
        }
    }

    public static YamlFile getFile(@NotNull String filename, String... path) {
        return FILES.stream()
                .filter(yamlFile -> Arrays.equals(yamlFile.directory, path))
                .filter(yamlFile -> yamlFile.filename.equals(filename))
                .findAny().orElseGet(() -> new YamlFile(filename, path));
    }

    public static void initialize() {
        plugin = JavaPlugin.getProvidingPlugin(YamlFile.class);
    }
}
