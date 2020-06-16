package client_side.ui.views;

import client_side.ui.Position;
import client_side.ui.view_models.MainWindowViewModelImpl;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.util.Arrays;

public class MainWindowController implements MainWindowView {
    @FXML
    Button connectButton;
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
    private MainWindowViewModelImpl vm;

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

    @FXML
    private void resetJoystick() {
        handleJoystickDragged(0, 0);
    }


    private int getRow(double yKm) {
        return 0;
    }

    private int getCol(double xKm) {
        return 0;
    }

    boolean pathCalculationInProgress = false;

    //TODO:
    //on click findPath check if bool above is true
    //save source and dest of last path calculation
    @Override
    public void setViewModel(MainWindowViewModelImpl vm) {
        this.vm = vm;

        vm.scriptProperty().bind(scriptTextArea.textProperty());
        vm.rudderValueProperty().bind(rudderSlider.valueProperty());
        vm.throttleValueProperty().bind(throttleSlider.valueProperty());
        vm.aileronValueProperty().bind(smallJoystickCircle.translateXProperty().divide(this.limit));
        vm.elevatorValueProperty().bind(smallJoystickCircle.translateYProperty().divide(-this.limit));

        map.planePositionProperty().bind(Bindings.createObjectBinding(() -> {
            if (!loadedData) return null;

            Point2D planePosition = vm.getPlanePosition();
            int row = getRow(planePosition.getY());
            int column = getCol(planePosition.getX());
            return new Position(row, column);
        }, vm.planePositionProperty()));
        map.planeHeadingProperty().bind(vm.planeHeadingProperty());

        vm.pathCalculated.addListener((observable, oldValue, newValue) -> {
            //map.drawPath(source,dest,path)
        });


    }


    private boolean loadedData = false;
    @FXML
    private void loadDataFromCSV() {
        loadedData = true;
        // TODO
        double[][] c = new double[256][256];
        for (int i = 0; i < c.length; i++)
            Arrays.fill(c[i], i);
        map.setPath(Arrays.asList(
                new Position(0, 0),
                new Position(0, 1),
                new Position(1, 1)));
        map.setElevations(c);
    }

    @FXML
    void connect(ActionEvent actionEvent) {
        // TODO: open pop up and get ip and port
        try {
            vm.connect("127.0.0.1", 5402);
            connectButton.setDisable(true);
        } catch (IOException e) {
            // TODO: open pop up
            e.printStackTrace();
        }
    }

    @FXML
    void autopilotClicked() {
        try {
            vm.startAutoPilotScript();
            scriptTextArea.setDisable(true);
        } catch (IllegalAccessException e) {
            // TODO: open popup script is already running
            e.printStackTrace();
        }
    }

    @FXML
    void manualClicked() {
        vm.stopAutoPilotScript();
        scriptTextArea.setDisable(false);
    }
}
