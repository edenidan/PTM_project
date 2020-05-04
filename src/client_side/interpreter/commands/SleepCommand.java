package client_side.interpreter.commands;

import client_side.interpreter.CannotInterpretException;
import client_side.interpreter.Classifier;
import client_side.interpreter.Command;
import client_side.interpreter.Variable;
import client_side.interpreter.math.ArithmeticParser;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class SleepCommand implements Command {
    private final ConcurrentMap<String, Variable> symbolTable;

    public SleepCommand(ConcurrentMap<String, Variable> symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public int doCommand(List<String> tokens, int startIndex) throws CannotInterpretException {
        double millis = ArithmeticParser.calc(tokens, startIndex + 1, symbolTable);
        if (!Classifier.isInt(millis))
            throw new CannotInterpretException("sleeping time must be an int of millis", startIndex);

        try {
            Thread.sleep((long) millis);
        } catch (InterruptedException e) {
            //don't sure if this throw is needed
            throw new CannotInterpretException("interrupted while sleeping", startIndex);
        }

        return ArithmeticParser.getEndOfExpression(tokens, startIndex, symbolTable) + 1;
    }
}
