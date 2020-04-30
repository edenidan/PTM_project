package client_side.interpreter;

public interface MathematicalExpressionParser {
    public Double calc(String[] tokens,int startIndex) throws CannotInterpretException;
}
