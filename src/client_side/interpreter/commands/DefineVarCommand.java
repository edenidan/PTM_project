package client_side.interpreter.commands;

import client_side.interpreter.Command;

import java.util.Map;

public class DefineVarCommand implements Command {
    private Map<String, Double> symbolTable;

    public DefineVarCommand(Map<String, Double> symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public int doCommand(String[] tokens, int startIndex) {
        // remember to check for "=" token after the identifier (variable name) token
        return 0;
    }
}
