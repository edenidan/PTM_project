package client_side.interpreter.commands;

import client_side.interpreter.CannotInterpretException;
import client_side.interpreter.Classifier;
import client_side.interpreter.Command;
import client_side.interpreter.Variable;
import client_side.interpreter.math.ArithmeticParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class AssignmentCommand implements Command {
    private final ConcurrentMap<String, Variable> symbolTable;
    ConcurrentMap<String, List<String>> property2bonded;
    ConcurrentMap<String, Double> infoFromServer;

    public AssignmentCommand(ConcurrentMap<String, Variable> symbolTable, ConcurrentMap<String, List<String>> property2bonded, ConcurrentMap<String, Double> infoFromServer) {
        this.symbolTable = symbolTable;
        this.property2bonded = property2bonded;
        this.infoFromServer = infoFromServer;
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
        String p = symbolTable.get(var).getBinding();
        if (p != null) {
            //Insert to q
                infoFromServer.put(p,value);

            for (String v : property2bonded.get(p))//for each var bounded update their values
                symbolTable.get(v).setValue(value);
        }

        return ArithmeticParser.getEndOfExpression(tokens, startIndex + 1, symbolTable) + 1;
    }

    private int bind(List<String> tokens, int startIndex) {
        String p = getProperty(tokens, startIndex + 2);
        String var = tokens.get(startIndex - 1);

        String oldProperty = symbolTable.get(var).getBinding();
        if (oldProperty != null)
            property2bonded.get(oldProperty).remove(var);//remove the var to the list of bounded to the old property

        symbolTable.get(var).setBinding(p);//link the var to the property

        symbolTable.get(var).setValue(infoFromServer.get(p));//change the value to the new property's value

        if(!property2bonded.containsKey(p))
            property2bonded.put(p,new ArrayList<>());

        property2bonded.get(p).add(var);//add the var to the list of bounded to that property


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
