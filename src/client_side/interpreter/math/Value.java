package client_side.interpreter.math;

public class Value<T> implements Expression<T> {
    private T value;

    public Value(T value) {
        this.value = value;
    }

    @Override
    public T calculate() {
        return value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
