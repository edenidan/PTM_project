package client_side.interpreter.commands;

import client_side.interpreter.Command;

import java.util.List;

public class OpenServerCommand implements Command {
    @Override
    public int doCommand(List<String> tokens, int startIndex) {
        // blocking call until starting to receive data
        return startIndex+3;
    }
}
