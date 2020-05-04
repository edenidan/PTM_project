package client_side.interpreter.commands;

import client_side.interpreter.Command;
import client_side.interpreter.EmptyObservable;

import java.util.List;

public class DisconnectCommand implements Command {
    private final EmptyObservable disconnect;

    public DisconnectCommand(EmptyObservable disconnect) {
        this.disconnect = disconnect;
    }

    @Override
    public int doCommand(List<String> tokens, int startIndex) {
        disconnect.setChangedAndNotify();
        return startIndex + 1;
    }
}
