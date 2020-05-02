package client_side.interpreter.commands;

import client_side.interpreter.CannotInterpretException;
import client_side.interpreter.Command;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class DefineVarCommand implements Command {
    private final ConcurrentMap<String, Double> symbolTable;

    public DefineVarCommand(ConcurrentMap<String, Double> symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public int doCommand(List<String> tokens, int startIndex) throws CannotInterpretException {
        if (tokens.size() - 1 == startIndex)
            throw new CannotInterpretException("Wrong usage of 'var' command", startIndex);

        String varName = tokens.get(startIndex + 1);
        if (symbolTable.get(varName) != null || "bind".equals(varName))//is already exists
            throw new CannotInterpretException(varName + " is already defined", startIndex);

        symbolTable.put(varName, null);

        return startIndex + 1;
    }
}
