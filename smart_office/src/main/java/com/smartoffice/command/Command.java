package com.smartoffice.command;

/**
 * Command interface.
 * Every operation in the system (book, cancel, add occupant) implements this.
 */
public interface Command {
    void execute();
}
