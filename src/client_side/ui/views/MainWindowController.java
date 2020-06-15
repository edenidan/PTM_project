package client_side.ui.views;

import client_side.ui.view_models.MainWindowViewModelImpl;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

public class MainWindowController implements MainWindowView {


    @FXML
    Circle bigJoystickCircle;
    @FXML
    Circle joystickCircle;
    @FXML
    TextArea scriptTextArea;
    @FXML
    Slider rudderSlider;
    @FXML
    Slider throttleSlider;
    @FXML
    ColoredMap map;

    private MainWindowViewModelImpl vm;

    DoubleProperty joystickX = new SimpleDoubleProperty();
    DoubleProperty joystickY = new SimpleDoubleProperty();

    private double limit;


    private void handleJoystickDragged(double x,double y){
        double mouseDistanceSquared = Math.pow(x, 2) + Math.pow(y, 2);

        double XToSet=x;
        double YToSet=y;

        if (mouseDistanceSquared > limit * limit) {
            double toScale =limit/Math.sqrt(mouseDistanceSquared);
            XToSet *= toScale;
            YToSet *= toScale;

        }
        joystickCircle.setTranslateX(XToSet);
        joystickCircle.setTranslateY(YToSet);

    }

    public void smallJoystickDragged(MouseEvent event) {
        handleJoystickDragged(
                event.getSceneX() - bigJoystickCircle.localToScene(0,0).getX(),
                event.getSceneY() - bigJoystickCircle.localToScene(0,0).getY());

    }

    public void bigJoystickDragged(MouseEvent event) {
        handleJoystickDragged(event.getX(),event.getY());
    }


    private int getRow(double yKm) {
        return 0;
    }

    private int getCol(double xKm) {
        return 0;
    }

    @FXML
    private void initialize(){
        this.limit = bigJoystickCircle.getRadius() - joystickCircle.getRadius();
    }
    @Override
    public void setViewModel(MainWindowViewModelImpl vm) {
        this.vm = vm;


        vm.script.bind(scriptTextArea.textProperty());
        vm.rudderValue.bind(rudderSlider.valueProperty());
        vm.throttleValue.bind(throttleSlider.valueProperty());

        vm.posChanged.addObserver((o, arg) ->
                map.setPlanePosition(getRow(vm.planeY), getCol(vm.planeX)));

        vm.planeHeading.addListener((observable, oldValue, newValue) ->
                map.setPlaneAngle(newValue.doubleValue()));


        vm.aileronValue.bind(joystickCircle.translateXProperty().divide(this.limit));
        vm.elevatorValue.bind(joystickCircle.translateYProperty().divide(-this.limit));
    }


}
