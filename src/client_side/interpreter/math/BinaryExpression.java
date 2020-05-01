package client_side.interpreter.math;

import java.util.function.BiFunction;

public class BinaryExpression implements Expression {
    protected Expression left, right;

    private int precedence;
    private BiFunction<Double,Double,Double> f;
    public BinaryExpression(BiFunction<Double,Double,Double> f,int precedence) {
        this.f=f;
        this.precedence=precedence;
    }

    public BinaryExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    public int getPrecedence(){
        return this.precedence;
    }

    @Override
    public double calculate(){
        return this.f.apply(left.calculate(),right.calculate());
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
