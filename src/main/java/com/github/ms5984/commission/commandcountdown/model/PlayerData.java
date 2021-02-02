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
import com.github.ms5984.commission.commandcountdown.api.CommandCounter;
import com.github.ms5984.commission.commandcountdown.api.PlayerCounter;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

public class PlayerData {
    private static final Supplier<NamespacedKey> DATA_KEY = CommandCountdown::getDataKey;
    private static final Map<Player, PlayerData> instances = new HashMap<>();
    private final Player player;
    private final Set<CommandCounter> playerLimits = new HashSet<>();

    private PlayerData(Player player) {
        this.player = player;
        loadFromPdc();
    }

    public void saveToPdc() {
        if (playerLimits.isEmpty()) {
            clearPdc();
            return;
        }
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            final BukkitObjectOutputStream outputStream = new BukkitObjectOutputStream(output);
            outputStream.writeObject(playerLimits);
            outputStream.flush();
            player.getPersistentDataContainer().set(DATA_KEY.get(), PersistentDataType.BYTE_ARRAY, output.toByteArray());
            System.out.println("Saved data to player");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromPdc() {
        final byte[] pdcContents = player.getPersistentDataContainer().get(DATA_KEY.get(), PersistentDataType.BYTE_ARRAY);
        if (pdcContents != null) {
            try {
                final BukkitObjectInputStream inputStream = new BukkitObjectInputStream(new ByteArrayInputStream(pdcContents));
                playerLimits.clear();
                //noinspection unchecked
                playerLimits.addAll((Set<CommandCounter>) inputStream.readObject());
                System.out.println("Successfully loaded data from player");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void clearPdc() {
        player.getPersistentDataContainer().remove(DATA_KEY.get());
    }

    public void storePlayerCounter(PlayerCounter playerCounter) {
        final int hashCode = playerCounter.hashCode(); // incorporates command hash+args
        playerLimits.stream().filter(cc -> cc.hashCode() == hashCode).findAny().ifPresent(playerLimits::remove);
        playerLimits.add(playerCounter);
    }

    public Set<CommandCounter> getPlayerLimits() {
        return playerLimits;
    }

    public static PlayerData getForPlayer(Player player) {
        return instances.computeIfAbsent(player, PlayerData::new);
    }

    public static void saveData() {
        instances.forEach((p, pd) -> pd.saveToPdc());
    }
}
