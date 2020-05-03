package client_side.interpreter;

import java.util.Observable;
import java.util.Observer;

public class Property extends Observable implements Observer {

    private final String property;
    private Double value;

    public Property(String name,Double value){
        this.property=name;
        this.value=value;
    }

    public String getName(){return this.property;}


    public void setValue(double value){
        this.value=value;
        setChanged();
        notifyObservers();
    }

    public double getValue(){return this.value;}

    @Override
    public void update(Observable o, Object arg) {
        this.value=(Double)arg;
    }
}
