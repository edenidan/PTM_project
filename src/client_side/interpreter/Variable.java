package client_side.interpreter;

public class Variable {
    private volatile double value;
    private volatile String binding;

    public Variable(double value) {
        this(value, null);
    }

    public Variable(double value, String binding) {
        this.value = value;
        this.binding = binding;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }
}
