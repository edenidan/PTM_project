package client_side.interpreter.commands;

import client_side.interpreter.*;
import client_side.interpreter.math.ArithmeticParser;

import java.util.ArrayList;
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

        Property p = symbolTable.get(var).getBinding();
        if(p!=null)
            toUpdate.add(new PropertyUpdate(p.getName(),value));

        return ArithmeticParser.getEndOfExpression(tokens, startIndex + 1, symbolTable) + 1;
    }

    private int bind(List<String> tokens, int startIndex) throws CannotInterpretException {
        String p = getProperty(tokens, startIndex + 2);
        String var = tokens.get(startIndex - 1);

        // TODO: 03/05/2020 maybe get rid of this if
        if(properties.get(p)==null)
            //properties.put(p,new Property(p,0.0));
            throw new CannotInterpretException("property is not exists",startIndex);
        
        try{ symbolTable.get(var).setBinding(properties.get(p));}
        catch (Exception e){throw new CannotInterpretException(e.getMessage(),startIndex);}

        return getEndOfProperty(tokens, startIndex+2)+1;
    }

    private String getProperty(List<String> tokens, int startIndex) {
        int endOfProperty = getEndOfProperty(tokens, startIndex);
        return String.join("", tokens.subList(startIndex, endOfProperty + 1));
    }

    private int getEndOfProperty(List<String> tokens, int startIndex) {
        for (int i = startIndex; i < tokens.size(); i++)
            if (!"/".equals(tokens.get(i)) && !"/".equals(tokens.get(i + 1)))
                return i;
        return -1;
    }
}
