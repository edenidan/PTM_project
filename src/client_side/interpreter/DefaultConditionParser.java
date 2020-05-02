package client_side.interpreter;

import client_side.interpreter.math.Parser;

import java.util.List;
import java.util.Map;

public class DefaultConditionParser implements ConditionParser {
    private final Map<String, Double> symbolTable;

    public DefaultConditionParser(Map<String, Double> symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public Boolean parse(List<String> tokens, int startIndex) throws CannotInterpretException {
        float operand1, operand2;
        Parser mp = new Parser(symbolTable);
        int operand1EndPos;
        try {
            operand1 = (float) mp.calc(tokens, startIndex);
            operand1EndPos = mp.getEndOfExpression(tokens, startIndex);

            operand2 = (float) mp.calc(tokens, operand1EndPos + /*operatorSize*/1 + 1);
        } catch (NumberFormatException e) {
            throw new CannotInterpretException("Cannot resolve condition operands", startIndex);
        }

        String operator = tokens.get(operand1EndPos + 1);
        switch (operator) {
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
            default:
                throw new CannotInterpretException("Unknown operator: " + operator, startIndex);
        }
    }
}
