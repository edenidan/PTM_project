package client_side.interpreter.commands;

import client_side.Wrapper;
import client_side.interpreter.Command;

import java.util.Map;

public class LoopCommand implements Command {
    private Map<String, Double> symbolTable;

    private Map<String, Command> commands;
    private Wrapper<Boolean> returned;

    public LoopCommand(Map<String, Double> symbolTable, Map<String, Command> commands, Wrapper<Boolean> returned) {
        this.symbolTable = symbolTable;
        this.commands = commands;
        this.returned = returned;
    }

    @Override
    public int doCommand(String[] tokens, int startIndex) {
        return 0;
    }
}
