package client_side.interpreter.commands;

import client_side.Wrapper;
import client_side.interpreter.CannotInterpretException;
import client_side.interpreter.Command;
import client_side.interpreter.math.ArithmeticParser;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class ReturnCommand implements Command {
    private final Wrapper<Boolean> returned;
    private final ConcurrentMap<String, Double> symbolTable;

    public ReturnCommand(ConcurrentMap<String, Double> symbolTable, Wrapper<Boolean> returned) {
        this.symbolTable = symbolTable;
        this.returned = returned;
    }

    @Override
    public int doCommand(List<String> tokens, int startIndex) throws CannotInterpretException {
        try {
            double value = ArithmeticParser.calc(tokens, startIndex + 1, symbolTable);
            if ((int) value != value) // not an int
                throw new CannotInterpretException("Cannot return a float", startIndex + 1);

            returned.set(true);
            return (int) value;
        } catch (NumberFormatException ex) {
            throw new CannotInterpretException("illegal return", startIndex);
        }
    }
}
