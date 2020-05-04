package client_side.interpreter;

import java.util.Observable;
import java.util.Observer;

public class Variable extends Observable {
    private double value;
    private Property boundProperty;

    private final Observer observer = (o, arg) -> this.value = (double) arg;

    public Variable(double value) {
        this.value = value;
        this.boundProperty = null;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        if (value == this.value)
            return;

        this.value = value;
        setChanged();
        notifyObservers(value);
    }

    public Property getBoundProperty() {
        return boundProperty;
    }

    public void setBoundProperty(Property property) {
        if (boundProperty != null) {
            boundProperty.deleteObserver(observer); // remove me listening for old property's changes
            this.deleteObserver(boundProperty); // remove old property listening for my changes
        }

        if (property != null) {
            property.addObserver(observer); // make me listen for property's changes
            this.addObserver(property); // make property listen for my changes

            value = property.getValue();
        }

        boundProperty = property;
    }
}
