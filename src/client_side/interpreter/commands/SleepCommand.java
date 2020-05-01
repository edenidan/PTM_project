package client_side.interpreter.commands;

import client_side.interpreter.Command;

import java.util.List;
import java.util.Map;

public class SleepCommand implements Command {
    private final Map<String, Double> symbolTable;

    public SleepCommand(Map<String, Double> symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public int doCommand(List<String> tokens, int startIndex) {
        return 0;
    }
}
