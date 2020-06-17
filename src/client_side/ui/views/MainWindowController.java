package client_side.ui.views;

import client_side.ui.Coordinate;
import client_side.ui.Position;
import client_side.ui.view_models.MainWindowViewModelImpl;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.List;

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

    Double originLatitude = null;
    Double originLongitude = null;
    Double mapCellSideLength = null;
    private int getRow(Coordinate current) throws IllegalAccessException {


        if(originLatitude == null || originLongitude == null)
            throw new IllegalAccessException("load from csv first");

        double originYKm =  111.320 * originLongitude * Math.cos(originLatitude);
        double currentYKm = 111.320 * current.getLongitude() * Math.cos(current.getLatitude());

        double dY = currentYKm - originYKm;

        return (int) (dY/mapCellSideLength);
    }

    private int getCol(Coordinate current) throws IllegalAccessException {

        if(originLatitude == null || originLongitude == null)
            throw new IllegalAccessException("load from csv first");

        double originXKm = originLatitude * 110.574;
        double currentXKm = current.getLatitude() * 110.574;

        double dX = currentXKm - originXKm;

        return (int) (dX/mapCellSideLength);
    }


    @Override
    public void setViewModel(MainWindowViewModelImpl vm) {
        this.vm = vm;

        vm.scriptProperty().bind(scriptTextArea.textProperty());
        vm.rudderValueProperty().bind(rudderSlider.valueProperty());
        vm.throttleValueProperty().bind(throttleSlider.valueProperty());
        vm.aileronValueProperty().bind(smallJoystickCircle.translateXProperty().divide(this.limit));
        vm.elevatorValueProperty().bind(smallJoystickCircle.translateYProperty().divide(-this.limit));

        map.planePositionProperty().bind(Bindings.createObjectBinding(() -> {
            Coordinate planePosition = vm.getPlanePosition();
            if (planePosition == null) return null;
            int row = getRow(planePosition);
            int column = getCol(planePosition);
            return new Position(row, column);
        }, vm.planePositionProperty()));
        map.planeHeadingProperty().bind(vm.planeHeadingProperty());

        vm.pathCalculated.addListener((observable, oldValue, newValue) -> {
            List<Position> path = TranslatePath(newValue);
            System.out.println(newValue);
            map.setPath(path);
            this.pathCalculationInProgress = false;
        });
    }


    int[][] mapData = null;

    @FXML
    private void loadDataFromCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CSV Data File");
        fileChooser.setInitialDirectory(new File("./resources"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File csvFile = fileChooser.showOpenDialog(null);
        if (csvFile == null) return;

        try {

            List<String> lines = Files.readAllLines(csvFile.toPath());
            this.originLatitude = Double.parseDouble(lines.get(0).split(",")[0]);
            this.originLongitude = Double.parseDouble(lines.get(0).split(",")[1]);

            this.mapCellSideLength = Math.sqrt(Double.parseDouble(lines.get(1).split(",")[0]));

            mapData = lines.stream()
                    .skip(2)
                    .map(line -> Arrays.stream(line.split(","))
                            .mapToInt(Integer::parseInt)
                            .toArray())
                    .toArray(int[][]::new);

            map.setElevations(mapData);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Error reading file").show();
        }
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

    boolean pathCalculationInProgress = false;
    Position lastPathCalculationSource = null;

    @FXML
    public void findPath() {
        if (this.pathCalculationInProgress)//todo: show error message
            return;
        this.pathCalculationInProgress = true;

        String ip = "127.0.0.1";
        int port = 1234;

        Position s = map.getPlanePosition();
        Position d = map.getMarkerPosition();
        this.lastPathCalculationSource = s;

        vm.calculatePath(ip, port, mapData, s.getRow(), s.getColumn(), d.getRow(), d.getColumn());
    }

    private List<Position> TranslatePath(String pathStr) {
        List<Position> result = new ArrayList<>();
        result.add(this.lastPathCalculationSource);

        String[] path = pathStr.split(",");
        Position prev = result.get(0);
        for (String step : path) {
            int prevR = prev.getRow();
            int prevC = prev.getColumn();
            Position newPos = null;

            if (step.equals("Up"))
                newPos = new Position(prevR - 1, prevC);

            if (step.equals("Down"))
                newPos = new Position(prevR + 1, prevC);

            if (step.equals("Right"))
                newPos = new Position(prevR, prevC + 1);

            if (step.equals("Up"))
                newPos = new Position(prevR, prevC - 1);
            prev = newPos;
            result.add(newPos);
        }

        return result;
    }
}
