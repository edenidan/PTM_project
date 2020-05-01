package client_side.interpreter;

import java.util.Map;

public class DefaultConditionParser implements ConditionParser {
    private final Map<String, Double> symbolTable;

    public DefaultConditionParser(Map<String, Double> symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public Boolean parse(String[] tokens, int startIndex) throws CannotInterpretException {
        double operand1, operand2;
        try {
            operand1 = getValue(tokens[startIndex]);
            operand2 = getValue(tokens[startIndex + /*operatorSize*/1 + 1]);
        } catch (NumberFormatException e) {
            throw new CannotInterpretException("Cannot resolve condition operands", startIndex);
        }

        switch (tokens[startIndex + 1]) {
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

    private double getValue(String operand) {
        Double value = symbolTable.get(operand);
        if (value == null) {
            value = Double.parseDouble(operand);
        }
        return value;
    }
}
