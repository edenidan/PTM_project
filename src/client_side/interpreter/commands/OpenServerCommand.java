package client_side.interpreter.commands;

import client_side.interpreter.Command;

public class OpenServerCommand implements Command {
    @Override
    public int doCommand(String[] tokens, int startIndex) {
        // blocking call until starting to receive data
        return 0;
    }
}
