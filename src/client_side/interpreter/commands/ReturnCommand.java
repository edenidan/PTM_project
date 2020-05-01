package client_side.interpreter.commands;

import client_side.Wrapper;
import client_side.interpreter.CannotInterpretException;
import client_side.interpreter.Command;
import client_side.interpreter.DefaultMathematicalExpressionParser;
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
            double value = new DefaultMathematicalExpressionParser(SymbolTable).calc(tokens,startIndex+1);
            if ((int) value != value)//not an int
                throw new CannotInterpretException("Cannot return a float", startIndex + 1);

            returned.set(true);
            return (int) value;
        } catch (NumberFormatException ex) {
                throw new CannotInterpretException("illegal return",startIndex);
        }

    }
}
