package client_side.interpreter.math;

public class BinaryExpression<T> implements Expression<T> {
    protected Expression<T> left, right;
    protected final BinaryOperator<T> operator;

    public BinaryExpression(BinaryOperator<T> operator) {
        this.operator = operator;
    }

    @Override
    public T calculate() {
        return operator.getFunction().apply(left.calculate(), right.calculate());
    }

    public int getPrecedence() {
        return operator.getPrecedence();
    }

    public Expression<T> getLeft() {
        return left;
    }

    public void setLeft(Expression<T> left) {
        this.left = left;
    }

    public Expression<T> getRight() {
        return right;
    }

    public void setRight(Expression<T> right) {
        this.right = right;
    }
}
