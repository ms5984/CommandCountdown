/*
 *  Copyright 2021 ms5984 (Matt) <https://github.com/ms5984>
 *
 *  This file is part of CommandCountdown.
 *
 *  CommandCountdown is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  CommandCountdown is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.ms5984.commission.commandcountdown.model;

import com.github.ms5984.commission.commandcountdown.api.PlayerCounter;
import org.bukkit.command.Command;

public class PlayerCounterImpl extends AbstractCounter implements PlayerCounter {

    private static final long serialVersionUID = 9016468129961710867L;
    private int count;

    public PlayerCounterImpl(Command command) {
        super(command);
        this.count = 0;
    }

    @Override
    public int getCurrentCount() {
        return count;
    }

    @Override
    public void setCurrentCount(int uses) {
        this.count = uses;
    }

    @Override
    public void increment() {
        if (count < limit) {
            ++count;
        }
    }

    @Override
    public void resetCurrentCount() {
        this.count = 0;
    }

    @Override
    public String toString() {
        return super.toString().replace("{count}", String.valueOf(count));
    }
}
