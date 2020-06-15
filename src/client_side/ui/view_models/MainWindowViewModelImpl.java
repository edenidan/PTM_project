package client_side.ui.view_models;

import javafx.beans.Observable;
import javafx.beans.property.*;
import utility.EmptyObservable;

public class MainWindowViewModelImpl implements MainWindowViewModel {


    public StringProperty script = new SimpleStringProperty();

    public DoubleProperty aileronValue = new SimpleDoubleProperty();
    public DoubleProperty elevatorValue = new SimpleDoubleProperty();
    public DoubleProperty rudderValue = new SimpleDoubleProperty();
    public DoubleProperty throttleValue = new SimpleDoubleProperty();

    public DoubleProperty planeHeading = new SimpleDoubleProperty();
    public Double planeX = null;
    public Double planeY = null;
    public EmptyObservable posChanged;

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
}
