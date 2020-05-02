package math.calculators;

import math.BinaryOperator;
import math.ValueParser;

import java.util.Map;

public class VariableCalculator<T> extends SimpleCalculator<T> {
    public VariableCalculator(
            Map<String, BinaryOperator<T>> binaryOperators,
            ValueParser<T> valueParser,
            Map<String, T> symbolTable) {
        super(binaryOperators, new ValueParser<T>() {
            @Override
            public boolean isValue(String token) {
                return symbolTable.containsKey(token) || valueParser.isValue(token);
            }

            @Override
            public T toValue(String token) throws IllegalArgumentException {
                if (symbolTable.containsKey(token))
                    return symbolTable.get(token);
                else
                    return valueParser.toValue(token);
            }
        });
    }
}
