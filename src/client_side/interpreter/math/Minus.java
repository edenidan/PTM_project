package client_side.interpreter.math;

public class Minus extends BinaryExpression {
    public Minus() {
    }

    public Minus(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public double calculate() {
        return left.calculate() - right.calculate();
    }

    @Override
    public int getPrecedence() {
        return 1;
    }
}
