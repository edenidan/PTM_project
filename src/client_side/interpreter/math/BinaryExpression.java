package client_side.interpreter.math;

import java.util.function.DoubleBinaryOperator;

public class BinaryExpression implements Expression {
    protected Expression left, right;

    protected final DoubleBinaryOperator operator;
    protected final int precedence;

    public BinaryExpression(DoubleBinaryOperator operator, int precedence) {
        this.operator = operator;
        this.precedence = precedence;
    }

    @Override
    public double calculate() {
        return operator.applyAsDouble(left.calculate(), right.calculate());
    }

    public int getPrecedence() {
        return this.precedence;
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
