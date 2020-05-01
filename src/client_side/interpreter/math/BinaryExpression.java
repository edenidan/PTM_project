package client_side.interpreter.math;

public abstract class BinaryExpression implements Expression {
    protected Expression left, right;

    public BinaryExpression() {
    }

    public BinaryExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    public abstract int getPrecedence();

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
