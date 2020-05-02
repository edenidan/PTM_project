package client_side.interpreter.math;

public class BinaryOperator<T> {
    private final java.util.function.BinaryOperator<T> function;
    private final int precedence;

    public BinaryOperator(java.util.function.BinaryOperator<T> function, int precedence) {
        this.function = function;
        this.precedence = precedence;
    }

    public java.util.function.BinaryOperator<T> getFunction() {
        return function;
    }

    public int getPrecedence() {
        return precedence;
    }
}
