package client_side.interpreter.math_parsers;

import client_side.interpreter.CannotInterpretException;
import client_side.interpreter.MathParser;
import math.Calculator;

import java.util.List;

public class CalculatorMathParser<T> implements MathParser<T> {
    private final Calculator<T> calculator;

    public CalculatorMathParser(Calculator<T> calculator) {
        this.calculator = calculator;
    }

    @Override
    public T calc(List<String> tokens, int startIndex) throws CannotInterpretException {
        int endIndex = getEndOfExpression(tokens, startIndex);
        try {
            return calculator.calcInfix(tokens.subList(startIndex, endIndex + 1));
        } catch (IllegalArgumentException e) {
            throw new CannotInterpretException(e.getMessage(), startIndex);
        }
    }

    @Override
    public int getEndOfExpression(List<String> tokens, int startIndex) {
        return calculator.getEndOfExpression(tokens.subList(startIndex, tokens.size())) + startIndex;
    }
}
