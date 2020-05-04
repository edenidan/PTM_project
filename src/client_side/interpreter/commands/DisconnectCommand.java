package client_side.interpreter.commands;

import client_side.Wrapper;
import client_side.interpreter.Command;

import java.util.List;

public class DisconnectCommand implements Command {
    private final Wrapper<Boolean> disconnect;

    public DisconnectCommand(Wrapper<Boolean> disconnect) {
        this.disconnect=disconnect;
    }

    @Override
    public int doCommand(List<String> tokens, int startIndex) {
        disconnect.set(true);
        return startIndex+1;
    }
}
