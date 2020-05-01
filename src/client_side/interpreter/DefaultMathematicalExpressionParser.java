package client_side.interpreter;

import java.util.List;
import java.util.Map;
import java.util.Stack;

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
        double res;
        try {
            res = Double.parseDouble(tokens.get(startIndex));
        } catch (NumberFormatException e) {
            res = 0;
        }
        return res;
    }


    public static int calc(String expression) throws IllegalArgumentException {
        Queue<Expression> queue = new LinkedList<>();
        Stack<Character> stack = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char character = expression.charAt(i);

            if (isDigit(character)) {
                Object[] result = readNextNumber(expression.substring(i));

                double number = (double) result[0];
                int numberLength = (int) result[1];

                queue.add(new Number(number));

                i += numberLength - 1;
            } else if (isOperator(character)) {
                int precedence = getPrecedence(character);

                while (!stack.empty() && isOperator(stack.peek()) && getPrecedence(stack.peek()) >= precedence)
                    queue.add(createBinExpressionFromOperator(stack.pop()));

                stack.push(character);
            } else if (character == '(') {
                stack.push(character);
            } else if (character == ')') {
                try {
                    while (stack.peek() != '(')
                        queue.add(createBinExpressionFromOperator(stack.pop()));
                } catch (EmptyStackException e) {
                    throw new IllegalArgumentException("Found a closing parentheses without a matching opening parentheses");
                }

                stack.pop();
            }
        }

        while (!stack.empty()) {
            if (stack.peek() == '(')
                throw new IllegalArgumentException("Too many opening parentheses");
            else
                queue.add(createBinExpressionFromOperator(stack.pop()));
        }

        Collections.reverse((LinkedList<Expression>) queue);
        return (int) createExpressionFromReversedPostfixQueue(queue).calculate();
    }

    private static boolean isDigit(char character) {
        return '0' <= character && character <= '9';
    }

    private static Object[] readNextNumber(String str) {
        int endIndex = 0;
        boolean decimalPointFound = false;

        for (char character : str.substring(1).toCharArray()) {
            if (!isDigit(character) && character != '.')
                break;

            if (character == '.')
                if (decimalPointFound)
                    break;
                else
                    decimalPointFound = true;

            endIndex++;
        }

        if (str.charAt(endIndex) == '.') {
            endIndex--;
            decimalPointFound = false;
        }

        double number;
        Scanner scanner = new Scanner(str.substring(0, endIndex + 1));
        if (decimalPointFound)
            number = scanner.nextDouble();
        else
            number = scanner.nextInt();

        return new Object[]{number, endIndex + 1};
    }

    private static boolean isOperator(char character) {
        return character == '+' || character == '-' || character == '*' || character == '/';
    }

    private static int getPrecedence(char operator) throws IllegalArgumentException {
        return createBinExpressionFromOperator(operator).getPrecedence();
    }

    private static BinaryExpression createBinExpressionFromOperator(char operator) throws IllegalArgumentException {
        switch (operator) {
            case '+':
                return new Plus();
            case '-':
                return new Minus();
            case '*':
                return new Mul();
            case '/':
                return new Div();
            default:
                throw new IllegalArgumentException("Tried to use a character that doesn't represent an operator");
        }
    }

    private static Expression createExpressionFromReversedPostfixQueue(Queue<Expression> queue) throws IllegalArgumentException {
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
}
