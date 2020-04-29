package client_side.interpreter.commands;

import client_side.interpreter.Command;

import java.util.Map;

public class IfCommand implements Command {
    private Map<String, Double> symbolTable;
    private Map<String, Command> commands;

    public IfCommand(Map<String, Double> symbolTable, Map<String, Command> commands) {
        this.symbolTable = symbolTable;
        this.commands = commands;
    }

    @Override
    public int doCommand(String[] tokens, int startIndex) {
        return 0;
    }
}
