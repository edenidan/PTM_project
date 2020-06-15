package client_side.ui.view_models;

import client_side.ui.models.Model;
import javafx.beans.property.*;
import utility.EmptyObservable;

import java.io.IOException;
import java.util.Observable;

public class MainWindowViewModelImpl implements MainWindowViewModel {


    public StringProperty script = new SimpleStringProperty();

    public DoubleProperty aileronValue = new SimpleDoubleProperty();
    public DoubleProperty elevatorValue = new SimpleDoubleProperty();
    public DoubleProperty rudderValue = new SimpleDoubleProperty();
    public DoubleProperty throttleValue = new SimpleDoubleProperty();

    public StringProperty pathCalculated = new SimpleStringProperty();

    public DoubleProperty planeHeading = new SimpleDoubleProperty();
    public Double planeX = null;
    public Double planeY = null;
    public EmptyObservable posChanged;
    Model m;

    EmptyObservable PathDoneObservable;

    public MainWindowViewModelImpl(Model m) {
        this.m = m;

        aileronValue.addListener((observable, oldValue, newValue) -> aileronChanged());
        elevatorValue.addListener((observable, oldValue, newValue) -> elevatorChanged());
        rudderValue.addListener((observable, oldValue, newValue) -> rudderChanged());
        throttleValue.addListener((observable, oldValue, newValue) -> throttleChanged());

        this.PathDoneObservable =  m.getPathDoneObservable();
        PathDoneObservable.addObserver(this);

    }

    @Override
    public void connect(String ip, int port) throws IOException {
        m.connect(ip,port);
    }

    @Override
    public void calculatePath(String ip, int port, double[][] heights, int sourceRow, int sourceCol, int destRow, int destCol) {
        m.calculatePath(ip,port,heights,sourceRow,sourceCol,destRow,destCol);
    }

    @Override
    public void startAutoPilotScript() throws IllegalAccessException {
        m.runScript(script.get());
    }

    @Override
    public void stopAutoPilotScript() {
        m.stopScript();
    }


    private void aileronChanged() {
        m.setAileron(aileronValue.get());
    }

    private void elevatorChanged() {
        m.setElevator(elevatorValue.get());
    }

    private void rudderChanged() {
        m.setRudder(rudderValue.get());

    }

    private void throttleChanged() {
        m.setThrottle(throttleValue.get());
    }

    @Override
    public void update(Observable o, Object arg) {
        //notifications from the model
        if(o == this.PathDoneObservable)
            pathCalculated.setValue(m.getPath());

    }
}
