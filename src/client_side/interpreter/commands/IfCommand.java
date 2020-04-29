package client_side.interpreter.commands;

import client_side.interpreter.Command;
import client_side.interpreter.ReturnCommand;

import java.util.Map;

public class IfCommand implements Command {
    private Map<String, Double> symbolTable;
    private Map<String, Command> commands;
    private ReturnCommand returnCommand;

    public IfCommand(Map<String, Double> symbolTable, Map<String, Command> commands, ReturnCommand returnCommand) {
        this.symbolTable = symbolTable;
        this.commands = commands;
        this.returnCommand = returnCommand;
    }

    @Override
    public int doCommand(String[] tokens, int startIndex) {
        return 0;
    }
}
