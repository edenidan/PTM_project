package client_side.interpreter;

import java.util.Map;

public class DefaultConditionParser implements ConditionParser {

    private final Map<String, Double> symbolTable;

    public DefaultConditionParser(Map<String, Double> symbolTable) {
        this.symbolTable = symbolTable;
    }

    private double getValue(String operand) {
        Double value = symbolTable.get(operand);
        if (value == null) {
            value = Double.parseDouble(operand);
        }
        return value;
    }

    private Boolean isOperator(String token) {
        if (token == null)
            return false;
        return token.equals("=")
                || token.equals("<")
                || token.equals(">")
                || token.equals("!");
    }

    private int getOperatorSize(String[] tokens, int startIndex) {
        int operatorSize = 0;
        operatorSize += isOperator(tokens[startIndex + 1]) ? 1 : 0;
        operatorSize += isOperator(tokens[startIndex + 2]) ? 1 : 0;

        return operatorSize;
    }

    @Override
    public Boolean parse(String[] tokens, int startIndex) throws CannotInterpretException {
        //the operator may be one token or two
        int operatorSize = getOperatorSize(tokens, startIndex);

        double operand1, operand2;
        try {
            operand1 = getValue(tokens[startIndex]);
            operand2 = getValue(tokens[startIndex + operatorSize + 1]);
        } catch (NumberFormatException e) {
            throw new CannotInterpretException("Cannot resolve condition operands", startIndex);
        }

        switch (tokens[startIndex + 1] + (operatorSize == 2 ? tokens[startIndex + 2] : "")) {
            case "==":
                return operand1 == operand2;
            case ">=":
                return operand1 >= operand2;
            case "<=":
                return operand1 <= operand2;
            case ">":
                return operand1 > operand2;
            case "<":
                return operand1 < operand2;
            case "!=":
                return operand1 != operand2;
        }
        throw new CannotInterpretException("Unknown operator", startIndex);

    }
}
