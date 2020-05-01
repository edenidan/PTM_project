package client_side.interpreter.math;

public class Mul extends BinaryExpression {
    public Mul() {
    }

    public Mul(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public double calculate() {
        return left.calculate() * right.calculate();
    }

    @Override
    public int getPrecedence() {
        return 2;
    }
}
