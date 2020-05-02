package client_side.interpreter;

import java.util.Optional;

public class Variable {
    private double value;
    private Optional<String> binding;

    public Variable(double value, Optional<String> binding) {
        this.value = value;
        this.binding = binding;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Optional<String> getBinding() {
        return binding;
    }

    public void setBinding(Optional<String> binding) {
        this.binding = binding;
    }
}
