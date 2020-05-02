package client_side.interpreter.math_parsers;

import math.BinaryOperator;
import math.ValueParser;
import math.calculators.SimpleCalculator;

import java.util.HashMap;
import java.util.Map;

public class LogicParser extends CalculatorMathParser<Boolean> {
    private static final math.Calculator<Boolean> CALCULATOR;

    public LogicParser() {
        super(CALCULATOR);
    }

    static {
        Map<String, BinaryOperator<Boolean>> binaryOperators = new HashMap<>();
        binaryOperators.put("==", new BinaryOperator<>((a, b) -> a == b, 3));
        binaryOperators.put("!=", new BinaryOperator<>((a, b) -> a != b, 3));
        binaryOperators.put("&&", new BinaryOperator<>((a, b) -> a && b, 2));
        binaryOperators.put("||", new BinaryOperator<>((a, b) -> a || b, 1));

        ValueParser<Boolean> valueParser = new ValueParser<Boolean>() {
            @Override
            public boolean isValue(String token) {
                return token.equals("true") || token.equals("false");
            }

            @Override
            public Boolean toValue(String token) throws IllegalArgumentException {
                if (!isValue(token))
                    throw new IllegalArgumentException(String.format("Cannot convert token \"%s\" to boolean", token));

                return token.equals("true");
            }
        };

        CALCULATOR = new SimpleCalculator<>(binaryOperators, valueParser);
    }
}
