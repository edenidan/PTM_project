package client_side.interpreter;

import java.util.Observable;
import java.util.Observer;

public class Property extends Observable implements Observer {
    private final String name;
    private double value;

    public Property(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
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

    @Override
    public void update(Observable o, Object arg) {
        setValue((double) arg);
    }
}
