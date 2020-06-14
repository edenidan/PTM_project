package client_side.ui.view_models;

import client_side.ui.models.Model;
import javafx.beans.property.*;
import javafx.geometry.Point2D;

import java.io.IOException;
import java.util.Observable;

public class MainWindowViewModelImpl implements MainWindowViewModel {
    Model m;

    private final StringProperty script = new SimpleStringProperty();

    private final DoubleProperty aileronValue = new SimpleDoubleProperty();
    private final DoubleProperty elevatorValue = new SimpleDoubleProperty();
    private final DoubleProperty rudderValue = new SimpleDoubleProperty();
    private final DoubleProperty throttleValue = new SimpleDoubleProperty();

    private final ReadOnlyDoubleWrapper planeHeading = new ReadOnlyDoubleWrapper();
    private final ReadOnlyObjectWrapper<Point2D> planePosition = new ReadOnlyObjectWrapper<>();

    public StringProperty pathCalculated = new SimpleStringProperty();
    Observable PathDoneObservable;

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

    public StringProperty scriptProperty() {
        return script;
    }

    public DoubleProperty aileronValueProperty() {
        return aileronValue;
    }

    public DoubleProperty elevatorValueProperty() {
        return elevatorValue;
    }

    public DoubleProperty rudderValueProperty() {
        return rudderValue;
    }

    public DoubleProperty throttleValueProperty() {
        return throttleValue;
    }

    public double getPlaneHeading() {
        return planeHeading.get();
    }

    public ReadOnlyDoubleProperty planeHeadingProperty() {
        return planeHeading.getReadOnlyProperty();
    }

    public Point2D getPlanePosition() {
        return planePosition.get();
    }

    public ReadOnlyObjectProperty<Point2D> planePositionProperty() {
        return planePosition.getReadOnlyProperty();
    }
}
