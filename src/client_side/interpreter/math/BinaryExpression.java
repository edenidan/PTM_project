package client_side.interpreter.math;

public class BinaryExpression implements Expression {
    protected Expression left, right;
    protected final BinaryOperator operator;

    public BinaryExpression(BinaryOperator operator) {
        this.operator = operator;
    }

    @Override
    public double calculate() {
        return operator.getFunction().applyAsDouble(left.calculate(), right.calculate());
    }

    public int getPrecedence() {
        return operator.getPrecedence();
    }

    public Expression getLeft() {
        return left;
    }

    public void setLeft(Expression left) {
        this.left = left;
    }

    public Expression getRight() {
        return right;
    }

    public void setRight(Expression right) {
        this.right = right;
    }
}
