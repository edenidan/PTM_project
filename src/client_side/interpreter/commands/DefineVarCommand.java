package client_side.interpreter.commands;

import client_side.interpreter.CannotInterpretException;
import client_side.interpreter.Command;

import java.util.Map;

public class DefineVarCommand implements Command {
    private final Map<String, Double> symbolTable;

    public DefineVarCommand(Map<String, Double> symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public int doCommand(String[] tokens, int startIndex) throws CannotInterpretException {
        if(tokens.length-1 == startIndex)
            throw new CannotInterpretException("Wrong usage of 'var' command",startIndex);

        String varName=tokens[startIndex+1];
        if(symbolTable.get(varName) != null)//is already exists
            throw new CannotInterpretException(varName+" is already defined",startIndex);

        symbolTable.put(varName,null);

        return startIndex+1;
    }
}
