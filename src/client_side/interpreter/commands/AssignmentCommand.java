package client_side.interpreter.commands;

import client_side.interpreter.CannotInterpretException;
import client_side.interpreter.Classifier;
import client_side.interpreter.Command;
import client_side.interpreter.math.ArithmeticParser;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class AssignmentCommand implements Command {
    private final ConcurrentMap<String, Double> symbolTable;

    public AssignmentCommand(ConcurrentMap<String, Double> symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public int doCommand(List<String> tokens, int startIndex) throws CannotInterpretException {

        String var = tokens.get(startIndex-1);
        if(!Classifier.isVariable(var,symbolTable))
            throw new CannotInterpretException("wrong usage of the operator =",startIndex);

        symbolTable.put(var,ArithmeticParser.calc(tokens,startIndex+1,symbolTable));
        return ArithmeticParser.getEndOfExpression(tokens,startIndex+1,symbolTable)+1;
    }
}
