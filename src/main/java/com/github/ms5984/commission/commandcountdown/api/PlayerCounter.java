package com.github.ms5984.commission.commandcountdown.api;

public interface PlayerCounter extends CommandCounter {
    /**
     * Get current number of uses.
     * @return number of times the command was used since
     * last reset of current count.
     */
    int getCurrentCount();

    /**
     * Set current number of uses.
     * @param uses new number of uses
     */
    void setCurrentCount(int uses);

    /**
     * Increment use count.
     */
    void increment();

    /**
     * Resets the current number of uses.
     */
    void resetCurrentCount();
}
