package client_side.interpreter;

import java.util.List;
import java.util.Map;

public class DefaultMathematicalExpressionParser implements MathematicalExpressionParser {

    private final Map<String, Double> symbolTable;

    public DefaultMathematicalExpressionParser(Map<String, Double> symbolTable) {
        this.symbolTable = symbolTable;
    }

    private boolean isOperator(String s) {
        return s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/") || s.equals("(") || s.equals(")");
    }

    private boolean isValue(String s) {
        if (symbolTable.containsKey(s))
            return true;
        try {
            double temp = Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public int getEndOfExpression(List<String> tokens, int startIndex) {
        //TODO
        return -1;
    }

    @Override
    public double calc(List<String> tokens, int startIndex) throws CannotInterpretException {
        int endIndex = getEndOfExpression(tokens, startIndex);
        //TODO
        return 0;
    }
}
