package client_side.interpreter.commands;

import client_side.interpreter.Command;
import client_side.interpreter.ReturnCommand;

import java.util.Map;

public class LoopCommand implements Command {
    private Map<String, Double> symbolTable;
    private Map<String, Command> commands;
    private ReturnCommand returnCommand;

    public LoopCommand(Map<String, Double> symbolTable, Map<String, Command> commands, ReturnCommand returnCommand) {
        this.symbolTable = symbolTable;
        this.commands = commands;
        this.returnCommand = returnCommand;
    }

    @Override
    public int doCommand(String[] tokens, int startIndex) {
        return 0;
    }
}
