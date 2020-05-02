package client_side.interpreter.math;

import client_side.interpreter.CannotInterpretException;

import java.util.*;

public class Parser {
    private final Map<String, Double> symbolTable;

    private static final Map<String, BinaryOperator> binaryOperators = new HashMap<>();

    public Parser(Map<String, Double> symbolTable) {
        this.symbolTable = symbolTable;
    }

    public double calc(List<String> tokens, int startIndex) throws CannotInterpretException {
        int endIndex = getEndOfExpression(tokens, startIndex);
        try {
            return calcWithoutRethrow(tokens, startIndex, endIndex);
        } catch (IllegalArgumentException e) {
            throw new CannotInterpretException(e.getMessage(), startIndex);
        }
    }

    private double calcWithoutRethrow(List<String> tokens, int startIndex, int endIndex) throws CannotInterpretException {
        LinkedList<Expression> queue = new LinkedList<>();
        Stack<String> stack = new Stack<>();

        for (int i = startIndex; i <= endIndex; i++) {
            String token = tokens.get(i);

            if (isNumber(token)) {
                queue.add(new Number(Double.parseDouble(token)));
            } else if (isVariable(token)) {
                queue.add(new Number(symbolTable.get(token)));
            } else if (isOperator(token)) {
                int precedence = getPrecedence(token);

                while (!stack.empty() && isOperator(stack.peek()) && getPrecedence(stack.peek()) >= precedence)
                    queue.add(createBinExpressionFromOperator(stack.pop()));

                stack.push(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                try {
                    while (!stack.peek().equals("("))
                        queue.add(createBinExpressionFromOperator(stack.pop()));
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
                queue.add(createBinExpressionFromOperator(stack.pop()));
        }


        Collections.reverse(queue);
        return createExpressionFromReversedPostfixQueue(queue).calculate();
    }

    public int getEndOfExpression(List<String> tokens, int startIndex) {
        for (int i = startIndex; i < tokens.size() - 1; i++) {
            if (isNumber(tokens.get(i)) || tokens.get(i).equals(")")) // possible end token
                if (!isOperator(tokens.get(i + 1)) && !tokens.get(i + 1).equals(")")) // possible token which means there is more
                    return i;
        }

        return tokens.size() - 1;
    }

    private boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isVariable(String token) {
        return symbolTable.containsKey(token);
    }

    private boolean isOperator(String token) {
        return binaryOperators.containsKey(token);
    }

    private int getPrecedence(String operator) throws IllegalArgumentException {
        if (!binaryOperators.containsKey(operator))
            throw new IllegalArgumentException("Cant find operator: " + operator);

        return binaryOperators.get(operator).getPrecedence();
    }

    private BinaryExpression createBinExpressionFromOperator(String operator) throws IllegalArgumentException {
        switch (operator) {
            case "+":
                //noinspection Convert2MethodRef
                return new BinaryExpression((a, b) -> a + b, 1);
            case "-":
                return new BinaryExpression((a, b) -> a - b, 1);
            case "*":
                return new BinaryExpression((a, b) -> a * b, 2);
            case "/":
                return new BinaryExpression((a, b) -> a / b, 2);
            default:
                throw new IllegalArgumentException("Tried to use a character that doesn't represent an operator");
        }
    }

    private Expression createExpressionFromReversedPostfixQueue(Queue<Expression> queue) throws IllegalArgumentException {
        if (queue.isEmpty())
            throw new IllegalArgumentException("Tried to create an expression from an empty queue");

        Expression expression = queue.remove();

        if (expression instanceof BinaryExpression) {
            BinaryExpression binaryExpression = (BinaryExpression) expression;
            binaryExpression.setRight(createExpressionFromReversedPostfixQueue(queue));
            binaryExpression.setLeft(createExpressionFromReversedPostfixQueue(queue));
        }

        return expression;
    }

    static {
        //noinspection Convert2MethodRef
        binaryOperators.put("+", new BinaryOperator((a, b) -> a + b, 1));
        binaryOperators.put("-", new BinaryOperator((a, b) -> a - b, 1));
        binaryOperators.put("*", new BinaryOperator((a, b) -> a * b, 2));
        binaryOperators.put("/", new BinaryOperator((a, b) -> a / b, 2));
    }
}
