package client_side.interpreter;

import java.util.HashMap;

public class DefaultMathematicalExpressionParser implements MathematicalExpressionParser {

    private final HashMap<String,Double> symbolTable;
    public DefaultMathematicalExpressionParser(HashMap<String,Double> symbolTable){
        this.symbolTable=symbolTable;
    }

    private boolean isOperator(String s){
        return "+".equals(s) || "-".equals(s) || "*".equals(s)
                || "/".equals(s) || "(".equals(s) || ")".equals(s);
    }
    private boolean isValue(String s){
        if(symbolTable.containsKey(s))
            return true;
        try{
            double temp = Double.parseDouble(s);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }


    public int getEndOfExpression(String[] tokens, int startIndex) {

        //TODO
        return -1;
    }

    @Override
    public Double calc(String[] tokens, int startIndex) throws CannotInterpretException {
        int endIndex = getEndOfExpression(tokens,startIndex);
        //TODO
        return null;
    }
}
