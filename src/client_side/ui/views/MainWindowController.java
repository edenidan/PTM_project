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
    Circle smallJoystickCircle;
    @FXML
    Circle bigJoystickCircle;
    @FXML
    TextArea scriptTextArea;
    @FXML
    Slider rudderSlider;
    @FXML
    Slider throttleSlider;
    @FXML
    ColoredMap map;

    private double limit;

    @FXML
    private void initialize() {
        this.limit = bigJoystickCircle.getRadius() - smallJoystickCircle.getRadius();
    }

    private void handleJoystickDragged(double x, double y) {
        double mouseDistanceSquared = Math.pow(x, 2) + Math.pow(y, 2);

        double XToSet = x;
        double YToSet = y;
        if (mouseDistanceSquared > limit * limit) {
            double toScale = limit / Math.sqrt(mouseDistanceSquared);
            XToSet *= toScale;
            YToSet *= toScale;

        }
        smallJoystickCircle.setTranslateX(XToSet);
        smallJoystickCircle.setTranslateY(YToSet);
    }

    @FXML
    private void smallJoystickDragged(MouseEvent event) {
        handleJoystickDragged(
                event.getSceneX() - bigJoystickCircle.localToScene(0, 0).getX(),
                event.getSceneY() - bigJoystickCircle.localToScene(0, 0).getY());
    }

    @FXML
    private void bigJoystickDragged(MouseEvent event) {
        handleJoystickDragged(event.getX(), event.getY());
    }


    private int getRow(double yKm) {
        return 0;
    }

    private int getCol(double xKm) {
        return 0;
    }

    @Override
    public void setViewModel(MainWindowViewModelImpl vm) {
        vm.script.bind(scriptTextArea.textProperty());
        vm.rudderValue.bind(rudderSlider.valueProperty());
        vm.throttleValue.bind(throttleSlider.valueProperty());
        vm.aileronValue.bind(smallJoystickCircle.translateXProperty().divide(this.limit));
        vm.elevatorValue.bind(smallJoystickCircle.translateYProperty().divide(-this.limit));

        vm.posChanged.addObserver((o, arg) -> map.setPlanePosition(getRow(vm.planeY), getCol(vm.planeX)));

        vm.planeHeading.addListener((observable, oldValue, newValue) -> map.setPlaneAngle(newValue.doubleValue()));
    }

    private double angle = 0; // TODO: delete this variable and actually implement loadDataFromCSV()

    @FXML
    private void loadDataFromCSV() {
        angle += 20;
        map.setPlaneAngle(angle);
        map.setElevations(new double[][]{{1, 5}, {2, 10}});
    }
}
