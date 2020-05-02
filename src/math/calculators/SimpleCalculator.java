package math.calculators;

import math.BinaryOperator;
import math.Calculator;
import math.ValueParser;

import java.util.*;

public class SimpleCalculator<T> implements Calculator<T> {
    private final Map<String, BinaryOperator<T>> binaryOperators;
    private final ValueParser<T> valueParser;

    public SimpleCalculator(Map<String, BinaryOperator<T>> binaryOperators, ValueParser<T> valueParser) {
        this.binaryOperators = binaryOperators;
        this.valueParser = valueParser;
    }

    @Override
    public T calcInfix(List<String> tokens) throws IllegalArgumentException {
        return calcPostfix(infixToPostfix(tokens));
    }

    @Override
    public int getEndOfExpression(List<String> tokens) {
        for (int i = 0; i < tokens.size() - 1; i++) {
            if (isValue(tokens.get(i)) || tokens.get(i).equals(")")) // possible end token
                if (!isOperator(tokens.get(i + 1)) && !tokens.get(i + 1).equals(")")) // possible token which means there is more
                    return i;
        }

        return tokens.size() - 1;
    }

    private LinkedList<String> infixToPostfix(List<String> tokens) throws IllegalArgumentException {
        LinkedList<String> queue = new LinkedList<>();
        Stack<String> stack = new Stack<>();

        for (String token : tokens) {
            if (isValue(token)) {
                queue.add(token);
            } else if (isOperator(token)) {
                int precedence = getPrecedence(token);

                while (!stack.empty() && isOperator(stack.peek()) && getPrecedence(stack.peek()) >= precedence)
                    queue.add(stack.pop());

                stack.push(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                try {
                    while (!stack.peek().equals("("))
                        queue.add(stack.pop());
                } catch (EmptyStackException e) {
                    throw new IllegalArgumentException("Found a closing parentheses without a matching opening parentheses");
                }

                stack.pop();
            }
        }

        while (!stack.empty()) {
            if (stack.peek().equals("("))
                throw new IllegalArgumentException("Too many opening parentheses");
            else
                queue.add(stack.pop());
        }

        return queue;
    }

    // Removes elements from tokens
    private T calcPostfix(LinkedList<String> tokens) throws IllegalArgumentException {
        if (tokens.isEmpty())
            throw new IllegalArgumentException("Tried to calculate with no tokens");

        String token = tokens.removeLast();

        if (isValue(token))
            return toValue(token);
        else if (isOperator(token)) {
            BinaryOperator<T> operator = binaryOperators.get(token);

            T rightOperand = calcPostfix(tokens);
            T leftOperand = calcPostfix(tokens);

            return operator.getFunction().apply(leftOperand, rightOperand);
        } else {
            throw new IllegalArgumentException("Tried to calculate with unknown token: " + token);
        }
    }

    private boolean isValue(String token) {
        return valueParser.isValue(token);
    }

    private T toValue(String token) throws IllegalArgumentException {
        return valueParser.toValue(token);
    }

    private boolean isOperator(String token) {
        return binaryOperators.containsKey(token);
    }

    private int getPrecedence(String operator) throws IllegalArgumentException {
        if (!binaryOperators.containsKey(operator))
            throw new IllegalArgumentException("Cant find operator: " + operator);

        return binaryOperators.get(operator).getPrecedence();
    }
}
