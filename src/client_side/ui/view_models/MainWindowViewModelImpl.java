package client_side.ui.view_models;

import client_side.ui.Coordinate;
import client_side.ui.models.Model;
import javafx.beans.property.*;

import java.io.IOException;
import java.util.Observable;

public class MainWindowViewModelImpl implements MainWindowViewModel {
    private final Model m;

    private final StringProperty script = new SimpleStringProperty();

    private final DoubleProperty aileronValue = new SimpleDoubleProperty();
    private final DoubleProperty elevatorValue = new SimpleDoubleProperty();
    private final DoubleProperty rudderValue = new SimpleDoubleProperty();
    private final DoubleProperty throttleValue = new SimpleDoubleProperty();


    private final ReadOnlyDoubleWrapper planeHeading = new ReadOnlyDoubleWrapper();
    private final ReadOnlyObjectWrapper<Coordinate> planePosition = new ReadOnlyObjectWrapper<>();

    public StringProperty pathCalculated = new SimpleStringProperty();
    Observable PathDoneObservable;
    Observable positionChangedObservable;
    Observable headingChanged;

    public MainWindowViewModelImpl(Model m) {
        this.m = m;

        aileronValue.addListener((observable, oldValue, newValue) -> aileronChanged());
        elevatorValue.addListener((observable, oldValue, newValue) -> elevatorChanged());
        rudderValue.addListener((observable, oldValue, newValue) -> rudderChanged());
        throttleValue.addListener((observable, oldValue, newValue) -> throttleChanged());

        this.PathDoneObservable = m.getPathDoneObservable();
        PathDoneObservable.addObserver((o, arg) -> pathCalculated.setValue(m.getPath()));

        this.positionChangedObservable = m.getPositionChangedObservable();
        positionChangedObservable.addObserver((o, arg) -> planePosition.set(m.getPlaneCoordinate()));

        this.headingChanged = m.getHeadingChangedObservable();
        headingChanged.addObserver((o, arg) -> planeHeading.set(m.getHeading()));

    }

    @Override
    public void connect(String ip, int port) throws IOException {
        m.connect(ip, port);
    }

    @Override
    public void calculatePath(String ip, int port, int[][] heights, int sourceRow, int sourceColumn, int destinationRow, int destinationColumn) {
        m.calculatePath(ip, port, heights, sourceRow, sourceColumn, destinationRow, destinationColumn);
    }

    @Override
    public void startAutoPilotScript() throws IllegalStateException {
        m.runScript(script.get());
    }

    @Override
    public void stopAutoPilotScript() {
        m.stopScript();
    }


    private void aileronChanged() {
        try {
            m.setAileron(aileronValue.get());
        } catch (IllegalAccessException | InterruptedException e) {
            //TODO: trigger an errorEmptyObservable no notify the view
        }
    }

    private void elevatorChanged() {
        try {
            m.setElevator(elevatorValue.get());
        } catch (IllegalAccessException | InterruptedException e) {
            //TODO: trigger an errorEmptyObservable no notify the view
        }
    }

    private void rudderChanged() {
        try {
            m.setRudder(rudderValue.get());
        } catch (IllegalAccessException | InterruptedException e) {
            //TODO: trigger an errorEmptyObservable no notify the view
        }
    }

    private void throttleChanged() {
        try {
            m.setThrottle(throttleValue.get());
        } catch (IllegalAccessException | InterruptedException e) {
            //TODO: trigger an errorEmptyObservable no notify the view
        }
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

    public Coordinate getPlanePosition() {
        return planePosition.get();
    }

    public ReadOnlyObjectProperty<Coordinate> planePositionProperty() {
        return planePosition.getReadOnlyProperty();
    }
}
