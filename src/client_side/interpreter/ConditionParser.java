package client_side.interpreter;

import client_side.interpreter.math.ArithmeticParser;

import java.util.List;
import java.util.Map;

public class ConditionParser {
    private ConditionParser() {
    }

    // startIndex: index of the first operand
    public static Boolean parse(List<String> tokens, int startIndex, Map<String, Double> symbolTable) throws CannotInterpretException {
        float operand1, operand2;
        int operand1EndPos;
        try {
            operand1 = (float) ArithmeticParser.calc(tokens, startIndex, symbolTable);
            operand1EndPos = ArithmeticParser.getEndOfExpression(tokens, startIndex);

            operand2 = (float) ArithmeticParser.calc(tokens, operand1EndPos + /*operatorSize*/1 + 1, symbolTable);
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
