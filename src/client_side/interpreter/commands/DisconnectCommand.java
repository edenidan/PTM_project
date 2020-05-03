package client_side.interpreter.commands;

import client_side.interpreter.Command;

import java.util.List;

public class DisconnectCommand implements Command {
    @Override
    public int doCommand(List<String> tokens, int startIndex) {
        return startIndex+1;
    }
}
