package client_side.interpreter;

import java.util.Optional;

public class Variable {
    private double value;
    private String binding;

    public Variable(double value){
        this(value,null);
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