package client_side.interpreter.math;

import java.util.function.DoubleBinaryOperator;

public class BinaryOperator {
    private final DoubleBinaryOperator function;
    private final int precedence;

    public BinaryOperator(DoubleBinaryOperator function, int precedence) {
        this.function = function;
        this.precedence = precedence;
    }

    public DoubleBinaryOperator getFunction() {
        return function;
    }

    public int getPrecedence() {
        return precedence;
    }
}
