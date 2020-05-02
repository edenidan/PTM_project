package client_side.interpreter.math;

import client_side.interpreter.CannotInterpretException;
import client_side.interpreter.Classifier;

import java.util.*;

public class ArithmeticParser {
    private ArithmeticParser() {
    }
    private static final Map<String, BinaryOperator> binaryOperators = new HashMap<>();


    public static double calc(List<String> tokens, int startIndex, Map<String, Double> symbolTable) throws CannotInterpretException {
        int endIndex = getEndOfExpression(tokens, startIndex,symbolTable);
        try {
            return calcWithoutRethrow(tokens, startIndex, endIndex, symbolTable);
        } catch (IllegalArgumentException e) {
            throw new CannotInterpretException(e.getMessage(), startIndex);
        }
    }

    public static int getEndOfExpression(List<String> tokens, int startIndex,Map<String,Double> symbolTable) {
        for (int i = startIndex; i < tokens.size() - 1; i++) {
            if (Classifier.isVariable(tokens.get(i),symbolTable) ||Classifier.isNumber(tokens.get(i)) || tokens.get(i).equals(")")) // possible end token
                if (!Classifier.isOperator(tokens.get(i + 1)) && !tokens.get(i + 1).equals(")")) // possible token which means there is more
                    return i;
        }

        return tokens.size() - 1;
    }

    private static double calcWithoutRethrow(List<String> tokens, int startIndex, int endIndex, Map<String, Double> symbolTable) throws CannotInterpretException, IllegalArgumentException {
        LinkedList<String> queue = new LinkedList<>();
        Stack<String> stack = new Stack<>();

        for (int i = startIndex; i <= endIndex; i++) {
            String token = tokens.get(i);

            if (Classifier.isNumber(token) || Classifier.isVariable(token, symbolTable)) {
                queue.add(token);
            } else if (Classifier.isOperator(token)) {
                int precedence = getPrecedence(token);

                while (!stack.empty() && Classifier.isOperator(stack.peek()) && getPrecedence(stack.peek()) >= precedence)
                    queue.add(stack.pop());

                stack.push(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                try {
                    while (!stack.peek().equals("("))
                        queue.add(stack.pop());
                } catch (EmptyStackException e) {
                    throw new CannotInterpretException("Found a closing parentheses without a matching opening parentheses", startIndex);
                }

                stack.pop();
            }
        }

        while (!stack.empty()) {
            if (stack.peek().equals("("))
                throw new CannotInterpretException("Too many opening parentheses", startIndex);
            else
                queue.add(stack.pop());
        }

        return createExpressionFromPostfix(queue, symbolTable).calculate();
    }


    private static int getPrecedence(String operator) throws IllegalArgumentException {
        if (!Classifier.isOperator(operator))
            throw new IllegalArgumentException("Cant find operator: " + operator);

        return binaryOperators.get(operator).getPrecedence();
    }

    private static Expression createExpressionFromPostfix(LinkedList<String> tokens, Map<String, Double> symbolTable) throws IllegalArgumentException {
        if (tokens.isEmpty())
            throw new IllegalArgumentException("Tried to create an expression with no tokens");

        String token = tokens.removeLast();

        if (Classifier.isNumber(token))
            return new Number(Double.parseDouble(token));
        else if (Classifier.isVariable(token, symbolTable))
            return new Number(symbolTable.get(token));
        else if (Classifier.isOperator(token)) {
            BinaryExpression binaryExpression = new BinaryExpression(binaryOperators.get(token));
            binaryExpression.setRight(createExpressionFromPostfix(tokens, symbolTable));
            binaryExpression.setLeft(createExpressionFromPostfix(tokens, symbolTable));

            return binaryExpression;
        } else {
            throw new IllegalArgumentException("Tried to create expression from unknown token: " + token);
        }
    }

    static {
        //noinspection Convert2MethodRef
        binaryOperators.put("+", new BinaryOperator((a, b) -> a + b, 1));
        binaryOperators.put("-", new BinaryOperator((a, b) -> a - b, 1));
        binaryOperators.put("*", new BinaryOperator((a, b) -> a * b, 2));
        binaryOperators.put("/", new BinaryOperator((a, b) -> a / b, 2));
    }
}
