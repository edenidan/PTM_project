package client_side.interpreter;

import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

public class Variable extends Observable implements Observer {
    private final String name;
    private double value;
    private Property boundProperty;

    public Variable(String name, double value) {
        this.name = name;
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

    public void setBoundProperty(Property property) throws Exception {
        if (boundProperty != null) {
            boundProperty.deleteObserver(this); // remove me listening for old property's changes
            this.deleteObserver(boundProperty); // remove old property listening for my changes
        }

        if (property != null) {
            property.addObserver(this); // make me listen for property's changes
            this.addObserver(property); // make property listen for my changes

            value = property.getValue();
        } else {
            throw new Exception("unknown property");
        }

        boundProperty = property;
    }

    @Override
    public void update(Observable o, Object arg) {
        this.value = (double) arg;
    }

    /*
    TODO if this method is for observer adding and removing to pick the right observer, it's probably unnecessary
      because comparing with address (the default) will work because we don't have multiple Variable objects for one variable
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variable variable = (Variable) o;
        return Objects.equals(name, variable.name);
    }
}
