package client_side.interpreter;

import java.util.Observable;
import java.util.Observer;

public class Variable extends Observable implements Observer {

    private final String name;
    private double value;
    private Property binding;

    public Variable(String name,double value) {
        this(name,value,null);
    }

    public Variable(String name,double value, Property binding) {
        this.name = name;
        this.value = value;
        this.binding = binding;
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

    public Property getBinding() {
        return binding;
    }

    public void setBinding(Property newBinding) throws Exception {
        if(binding!=null)
            binding.deleteObserver(this);

        binding = newBinding;

        if(newBinding == null)
            throw new Exception("unknown property");
        newBinding.addObserver(this);
        addObserver(newBinding);
        this.value=newBinding.getValue();

    }

    @Override
    public void update(Observable o, Object arg) {
        this.value=(Double)arg;
    }

    @Override
    public boolean equals(Object o){
        Variable other;
        try { other = (Variable) o; }
        catch (Exception e){return false;}

        return other.getName().equals(this.getName());
    }
}
