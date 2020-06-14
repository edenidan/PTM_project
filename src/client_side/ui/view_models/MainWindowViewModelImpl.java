package client_side.ui.view_models;

import javafx.beans.property.*;
import javafx.geometry.Point2D;

public class MainWindowViewModelImpl implements MainWindowViewModel {
    private final StringProperty script = new SimpleStringProperty();

    private final DoubleProperty aileronValue = new SimpleDoubleProperty();
    private final DoubleProperty elevatorValue = new SimpleDoubleProperty();
    private final DoubleProperty rudderValue = new SimpleDoubleProperty();
    private final DoubleProperty throttleValue = new SimpleDoubleProperty();

    private final ReadOnlyDoubleWrapper planeHeading = new ReadOnlyDoubleWrapper();
    private final ReadOnlyObjectWrapper<Point2D> planePosition = new ReadOnlyObjectWrapper<>();

    public MainWindowViewModelImpl() {
        aileronValue.addListener((observable, oldValue, newValue) -> aileronChanged());
        elevatorValue.addListener((observable, oldValue, newValue) -> elevatorChanged());
        rudderValue.addListener((observable, oldValue, newValue) -> rudderChanged());
        throttleValue.addListener((observable, oldValue, newValue) -> throttleChanged());
    }

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


    private void aileronChanged() {
    }

    private void elevatorChanged() {
    }

    private void rudderChanged() {
    }

    private void throttleChanged() {
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
