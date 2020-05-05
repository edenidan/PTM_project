package client_side.interpreter.commands;

import client_side.Wrapper;
import client_side.interpreter.CannotInterpretException;
import client_side.interpreter.Classifier;
import client_side.interpreter.Command;
import client_side.interpreter.Variable;
import client_side.interpreter.math.ArithmeticParser;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class ReturnCommand implements Command {
    private final Wrapper<Integer> returned;
    private final ConcurrentMap<String, Variable> symbolTable;

    public ReturnCommand(ConcurrentMap<String, Variable> symbolTable, Wrapper<Integer> returned) {
        this.symbolTable = symbolTable;
        this.returned = returned;
    }

    @Override
    public int doCommand(List<String> tokens, int startIndex) throws CannotInterpretException {
        double value = ArithmeticParser.calc(tokens, startIndex + 1, symbolTable);
        if (!Classifier.isInt(value)) // not an int
            throw new CannotInterpretException("Cannot return a float", startIndex + 1);

        returned.set((int) value);
        return ArithmeticParser.getEndOfExpression(tokens, startIndex + 1, symbolTable) + 1; // supposed to be ignored
    }
}
