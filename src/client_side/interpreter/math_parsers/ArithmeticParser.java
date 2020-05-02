package client_side.interpreter.math_parsers;

import math.BinaryOperator;
import math.ValueParser;
import math.calculators.VariableCalculator;

import java.util.HashMap;
import java.util.Map;

public class ArithmeticParser extends CalculatorMathParser<Double> {
    private static final Map<String, BinaryOperator<Double>> BINARY_OPERATORS;
    private static final ValueParser<Double> VALUE_PARSER;

    public ArithmeticParser(Map<String, Double> symbolTable) {
        super(new VariableCalculator<>(BINARY_OPERATORS, VALUE_PARSER, symbolTable));
    }

    static {
        BINARY_OPERATORS = new HashMap<>();
        //noinspection Convert2MethodRef
        BINARY_OPERATORS.put("+", new BinaryOperator<>((a, b) -> a + b, 1));
        BINARY_OPERATORS.put("-", new BinaryOperator<>((a, b) -> a - b, 1));
        BINARY_OPERATORS.put("*", new BinaryOperator<>((a, b) -> a * b, 2));
        BINARY_OPERATORS.put("/", new BinaryOperator<>((a, b) -> a / b, 2));

        VALUE_PARSER = new ValueParser<Double>() {
            @Override
            public boolean isValue(String token) {
                try {
                    Double.parseDouble(token);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }

            @Override
            public Double toValue(String token) throws IllegalArgumentException {
                try {
                    return Double.parseDouble(token);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(String.format("Cannot convert token \"%s\" to double", token), e);
                }
            }
        };
    }
}
