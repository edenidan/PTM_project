package client_side.interpreter.commands;

import client_side.interpreter.*;
import client_side.interpreter.math.ArithmeticParser;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

public class AssignmentCommand implements Command {
    private final ConcurrentMap<String, Variable> symbolTable;
    ConcurrentMap<String, Property> properties;
    BlockingQueue<PropertyUpdate> toUpdate;

    public AssignmentCommand(ConcurrentMap<String, Variable> symbolTable, ConcurrentMap<String, Property> properties, BlockingQueue<PropertyUpdate> toUpdate) {
        this.symbolTable = symbolTable;
        this.properties = properties;
        this.toUpdate = toUpdate;
    }

    @Override
    public int doCommand(List<String> tokens, int startIndex) throws CannotInterpretException {

        String var = tokens.get(startIndex - 1);
        if (!Classifier.isVariable(var, symbolTable))
            throw new CannotInterpretException("wrong usage of the operator =", startIndex);

        if (tokens.get(startIndex + 1).equals("bind"))
            return bind(tokens, startIndex);

        double value = ArithmeticParser.calc(tokens, startIndex + 1, symbolTable);
        symbolTable.get(var).setValue(value);

        Property p = symbolTable.get(var).getBoundProperty();
        if (p != null)
            toUpdate.add(new PropertyUpdate(p.getName(), value));

        return ArithmeticParser.getEndOfExpression(tokens, startIndex + 1, symbolTable) + 1;
    }

    private int bind(List<String> tokens, int startIndex) throws CannotInterpretException {
        String propertyName;
        try {
            propertyName = getPropertyName(tokens, startIndex + 2);
        } catch (IllegalArgumentException e) {
            throw new CannotInterpretException("Cannot interpret name of binding", startIndex + 2);
        }

        if (!properties.containsKey(propertyName))
            throw new CannotInterpretException("property does not exist", startIndex);

        String variableName = tokens.get(startIndex - 1);
        symbolTable.get(variableName).setBoundProperty(properties.get(propertyName));

        return getEndOfPropertyName(tokens, startIndex + 2) + 1;
    }

    private static String getPropertyName(List<String> tokens, int startIndex) throws IllegalArgumentException {
        int endOfProperty = getEndOfPropertyName(tokens, startIndex);
        return String.join("", tokens.subList(startIndex, endOfProperty + 1));
    }

    private static int getEndOfPropertyName(List<String> tokens, int startIndex) throws IllegalArgumentException {
        for (int i = startIndex; i < tokens.size() - 1; i++)
            if (!tokens.get(i).equals("/") && !tokens.get(i).equals("-")) // word
                if (!tokens.get(i + 1).equals("/") && !tokens.get(i + 1).equals("-")) // isn't followed by /
                    return i;

        if (!tokens.get(tokens.size() - 1).equals("/"))
            return tokens.size() - 1;
        else
            throw new IllegalArgumentException("No property found");
    }
}
