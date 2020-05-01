package client_side.interpreter.math;

public class Div extends BinaryExpression {
    public Div() {
    }

    public Div(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public double calculate() {
        return left.calculate() / right.calculate();
    }

    @Override
    public int getPrecedence() {
        return 2;
    }
}
