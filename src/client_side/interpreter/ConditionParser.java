package client_side.interpreter;

import client_side.interpreter.math.ArithmeticParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class ConditionParser {
    private ConditionParser() {
    }

    private static final Map<String, MyPredicate> operators = new HashMap<>();

    // startIndex: index of the first operand
    public static Boolean parse(List<String> tokens, int startIndex, ConcurrentMap<String, Variable> symbolTable) throws CannotInterpretException {
        int operand1EndPos = ArithmeticParser.getEndOfExpression(tokens, startIndex, symbolTable);

        float operand1 = (float) ArithmeticParser.calc(tokens, startIndex, symbolTable);
        float operand2 = (float) ArithmeticParser.calc(tokens, operand1EndPos + 2, symbolTable);

        String operatorToken = tokens.get(operand1EndPos + 1);
        MyPredicate operator = operators.get(operatorToken);
        if (operator == null)
            throw new CannotInterpretException("Unknown operator: " + operatorToken, startIndex);

        return operator.test(operand1, operand2);
    }

    static {
        operators.put("==", (a, b) -> a == b);
        operators.put("!=", (a, b) -> a != b);
        operators.put("<=", (a, b) -> a <= b);
        operators.put(">=", (a, b) -> a >= b);
        operators.put("<", (a, b) -> a < b);
        operators.put(">", (a, b) -> a > b);
    }

    // predicate with float parameters, because we cant use Generic interfaces because they don't work with primitives
    private interface MyPredicate {
        boolean test(float a, float b);
    }
}
