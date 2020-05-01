package client_side.interpreter;

public interface MathematicalExpressionParser {
    Double calc(String[] tokens,int startIndex) throws CannotInterpretException;
    public int getEndOfExpression(String[] tokens, int startIndex) throws CannotInterpretException;

    }
