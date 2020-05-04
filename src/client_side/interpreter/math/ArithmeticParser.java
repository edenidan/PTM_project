package client_side.interpreter.math;

import client_side.interpreter.CannotInterpretException;
import client_side.interpreter.Classifier;
import client_side.interpreter.Variable;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

public class ArithmeticParser {
    private ArithmeticParser() {
    }

    public static double calc(List<String> tokens, int startIndex, ConcurrentMap<String, Variable> symbolTable) throws CannotInterpretException {
        int endIndex = getEndOfExpression(tokens, startIndex, symbolTable);
        try {
            return calcInfix(tokens.subList(startIndex, endIndex + 1), symbolTable);
        } catch (IllegalArgumentException e) {
            throw new CannotInterpretException(e.getMessage(), startIndex);
        }
    }

    public static int getEndOfExpression(List<String> tokens, int startIndex, ConcurrentMap<String, Variable> symbolTable) {
        for (int i = startIndex; i < tokens.size() - 1; i++) {
            if (Classifier.isVariable(tokens.get(i), symbolTable) || Classifier.isNumber(tokens.get(i)) || tokens.get(i).equals(")")) // possible end token
                if (!Classifier.isOperator(tokens.get(i + 1)) && !tokens.get(i + 1).equals(")")) // possible token which means there is more
                    return i;
        }

        return tokens.size() - 1;
    }

    private static double calcInfix(List<String> tokens, ConcurrentMap<String, Variable> symbolTable) throws IllegalArgumentException {
        LinkedList<String> postfix = infixToPostfix(tokens, symbolTable);
        return calcPostfix(postfix, symbolTable);
    }

    private static LinkedList<String> infixToPostfix(List<String> tokens, ConcurrentMap<String, Variable> symbolTable) {
        LinkedList<String> queue = new LinkedList<>();
        Stack<String> stack = new Stack<>();

        for (String token : tokens) {
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

    private static final Map<String, BinaryOperator> binaryOperators = new HashMap<>();

    // Warning: removes elements from tokens parameter
    private static double calcPostfix(LinkedList<String> tokens, ConcurrentMap<String, Variable> symbolTable) throws IllegalArgumentException {
        if (tokens.isEmpty())
            throw new IllegalArgumentException("Tried to calculate with no tokens");

        String token = tokens.removeLast();

        if (Classifier.isNumber(token))
            return Double.parseDouble(token);
        else if (Classifier.isVariable(token, symbolTable))
            return symbolTable.get(token).getValue();
        else if (Classifier.isOperator(token)) {
            BinaryOperator operator = binaryOperators.get(token);

            double rightOperand = calcPostfix(tokens, symbolTable);
            double leftOperand = calcPostfix(tokens, symbolTable);

            return operator.getFunction().applyAsDouble(leftOperand, rightOperand);
        } else {
            throw new IllegalArgumentException("Tried to calculate with unknown token: " + token);
        }
    }

    private static int getPrecedence(String operator) throws IllegalArgumentException {
        if (!Classifier.isOperator(operator))
            throw new IllegalArgumentException("Cant find operator: " + operator);

        return binaryOperators.get(operator).getPrecedence();
    }

    static {
        //noinspection Convert2MethodRef
        binaryOperators.put("+", new BinaryOperator((a, b) -> a + b, 1));
        binaryOperators.put("-", new BinaryOperator((a, b) -> a - b, 1));
        binaryOperators.put("*", new BinaryOperator((a, b) -> a * b, 2));
        binaryOperators.put("/", new BinaryOperator((a, b) -> a / b, 2));
    }
}
