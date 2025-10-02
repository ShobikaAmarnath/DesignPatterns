package com.smartoffice.command;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Invoker class holds and executes commands.
 * Can also keep history if undo/redo is needed.
 */
public class CommandInvoker {
    private final Deque<Command> history = new ArrayDeque<>();

    public void executeCommand(Command cmd) {
        cmd.execute();
        history.push(cmd);
    }

    public int getHistorySize() {
        return history.size();
    }
}
