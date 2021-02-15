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

import com.github.ms5984.commission.commandcountdown.api.DefaultCounter;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DefaultCounterImpl extends AbstractCounter implements DefaultCounter {

    private static final long serialVersionUID = -5047686256799959507L;
    private static final Map<OfflinePlayer, UUID> playerUUIDMap = new ConcurrentHashMap<>();
    protected final Map<UUID, Integer> counts = new ConcurrentHashMap<>();

    public DefaultCounterImpl(Command command) {
        super(command);
    }

    @Override
    public int getCurrentCount(OfflinePlayer player) {
        return counts.getOrDefault(playerUUIDMap.computeIfAbsent(player, OfflinePlayer::getUniqueId),0);
    }

    @Override
    public void setCurrentCount(OfflinePlayer player, int uses) {
        counts.put(playerUUIDMap.computeIfAbsent(player, OfflinePlayer::getUniqueId), uses);
    }

    @Override
    public void increment(OfflinePlayer player) {
        setCurrentCount(player, getCurrentCount(player) + 1);
    }

    @Override
    public void resetCurrentCount(OfflinePlayer player) {
        counts.remove(playerUUIDMap.computeIfAbsent(player, OfflinePlayer::getUniqueId));
    }
}
