package client_side.interpreter;

import java.util.Observable;

public class EmptyObservable extends Observable {
    public void setChangedAndNotify() {
        setChanged();
        notifyObservers();
    }
}
