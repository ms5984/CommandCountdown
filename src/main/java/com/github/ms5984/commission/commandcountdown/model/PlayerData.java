package com.github.ms5984.commission.commandcountdown.model;

import com.github.ms5984.commission.commandcountdown.api.PlayerCounter;
import lombok.val;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData {
    private static final Map<Player, PlayerData> instances = new ConcurrentHashMap<>();
    private final Set<PlayerCounter> playerCounters = new HashSet<>();
    private final YamlFile yamlFile;
    private transient Player player;

    private PlayerData(Player player) {
        this.player = player;
        this.yamlFile = YamlFile.getFile(player.getUniqueId().toString(), "players");
    }

    public void clear() {
        yamlFile.delete();
    }

    /*
    File spec
    filename = playerUuid.yml
     */

    public void loadFromFile() {
        if (!yamlFile.exists()) {
            return;
        }
        for (String node : yamlFile.getConfig().getKeys(false)) {
        }
    }

    public void saveToFile() {
        yamlFile.save();;
    }

    public void storePlayerCounter(PlayerCounter playerCounter) {
        val hc = playerCounter.hashCode();
        playerCounters.removeIf(pc -> pc.hashCode() == hc);
        playerCounters.add(playerCounter);
    }

    public Set<PlayerCounter> getPlayerLimits() {
        return playerCounters;
    }

    public static PlayerData getForPlayer(Player player) {
        return instances.computeIfAbsent(player, PlayerData::new);
    }

    public static void saveData() {
        instances.values().forEach(PlayerData::saveToFile);
    }
}
