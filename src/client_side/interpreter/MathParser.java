package client_side.interpreter;

import java.util.List;

public interface MathParser<T> {
    T calc(List<String> tokens, int startIndex) throws CannotInterpretException;

    int getEndOfExpression(List<String> tokens, int startIndex);
}
