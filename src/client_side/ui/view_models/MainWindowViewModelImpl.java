package client_side.ui.view_models;

import javafx.beans.Observable;
import javafx.beans.property.*;
import utility.EmptyObservable;

public class MainWindowViewModelImpl implements MainWindowViewModel {



    public StringProperty script = new SimpleStringProperty();

    public DoubleProperty joystickX = new SimpleDoubleProperty();
    public DoubleProperty joystickY = new SimpleDoubleProperty();
    public DoubleProperty rudderValue = new SimpleDoubleProperty();
    public DoubleProperty throttleValue = new SimpleDoubleProperty();

    public DoubleProperty planeHeading = new SimpleDoubleProperty();
    public Double planeX = null;
    public Double planeY = null;
    public EmptyObservable posChanged;


    @Override
    public void connect(String ip, int port) {

    }

    @Override
    public void calculatePath(String ip, int port) {

    }

    @Override
    public void startAutoPilotScript() {

    }

    @Override
    public void stopAutoPilotScript() {

    }


    @Override
    public void joystickChanged() {

    }

    @Override
    public void rudderChanged() {

    }

    @Override
    public void throttleChanged() {

    }
}
