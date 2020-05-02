package math;

import java.util.List;

public interface Calculator<T> {
    T calcInfix(List<String> tokens) throws IllegalArgumentException;

    int getEndOfExpression(List<String> tokens);
}
