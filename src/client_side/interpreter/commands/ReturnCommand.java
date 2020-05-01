package client_side.interpreter.commands;

import client_side.Wrapper;
import client_side.interpreter.CannotInterpretException;
import client_side.interpreter.Command;
import test.MainTrain;

import java.util.Map;

public class ReturnCommand implements Command {
    private final Wrapper<Boolean> returned;
    private final Map<String, Double> SymbolTable;

    public ReturnCommand(Map<String, Double> symbolTable, Wrapper<Boolean> returned) {
        this.SymbolTable = symbolTable;
        this.returned = returned;
    }

    @Override
    public int doCommand(String[] tokens, int startIndex) throws CannotInterpretException {
        try {
            double value = Double.parseDouble(tokens[startIndex + 1]);
            if ((int) value != value)//not an int
                throw new CannotInterpretException("Cannot return a float", startIndex + 1);

            returned.set(true);
            return (int) value;
        } catch (NumberFormatException ex) {
            if (MainTrain.debug)
                System.out.println("number parsing failed.");
        }

        Double retVal = SymbolTable.get(tokens[startIndex + 1]);
        if (retVal == null)
            throw new CannotInterpretException("Cannot find symbol: " + tokens[startIndex + 1], startIndex + 1);
        if (Math.floor(retVal) != retVal)
            throw new CannotInterpretException("Cannot return a float", startIndex + 1);

        returned.set(true);
        return (int) Math.floor(retVal);
    }
}
