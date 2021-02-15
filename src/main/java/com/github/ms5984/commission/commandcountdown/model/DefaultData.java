package com.github.ms5984.commission.commandcountdown.model;

import com.github.ms5984.commission.commandcountdown.api.DefaultCounter;
import com.github.ms5984.commission.commandcountdown.util.CommandUtil;
import lombok.val;
import org.bukkit.command.Command;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultData {
    private static final Map<Command, DefaultData> instances = new ConcurrentHashMap<>();
    private final Set<DefaultCounter> commandCounters = new HashSet<>();
    private final YamlFile yamlFile;
    private transient Command command;

    private DefaultData(Command command) {
        this.command = command;
        this.yamlFile = CommandUtil.getLabels(command).parallelStream()
                .map(label -> label.replace(":", "-"))
                .map(n -> YamlFile.getFile(n, "defaults"))
                .filter(YamlFile::exists)
                .findAny()
                .orElseGet(() -> CommandUtil.getFallbackPrefixedLabel(command).stream()
                        .findAny()
                        .map(label -> YamlFile.getFile(label, "defaults"))
                        .orElseThrow(IllegalArgumentException::new));
    }

    public void clear() {
        yamlFile.delete();
    }

    public void saveToFile() {
        yamlFile.save();;
    }

    public void storeCounter(DefaultCounter counter) {
        val hc = counter.hashCode();
        commandCounters.removeIf(pc -> pc.hashCode() == hc);
        commandCounters.add(counter);
    }

    public Set<DefaultCounter> getDefaultLimits() {
        return commandCounters;
    }

    public static DefaultData get(Command command) {
        return instances.computeIfAbsent(command, DefaultData::new);
    }

    public static void saveData() {
        instances.values().forEach(DefaultData::saveToFile);
    }
}
