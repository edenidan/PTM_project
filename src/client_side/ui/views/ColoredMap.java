package client_side.ui.views;

import client_side.ui.Position;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

public class ColoredMap extends Canvas {
    private final ObjectProperty<Position> planePosition = new SimpleObjectProperty<>();
    private final DoubleProperty planeHeading = new SimpleDoubleProperty();

    private List<List<Color>> elevationColors;

    private Position markerPosition;

    private List<Position> path;

    private final StringProperty planeImageFileName = new SimpleStringProperty();
    private Image planeImage;
    private final StringProperty markerImageFileName = new SimpleStringProperty();
    private Image markerImage;

    private Double cellWidth, cellHeight;

    public ColoredMap() {
        planePosition.addListener((observable, oldValue, newValue) -> draw());
        planeHeading.addListener((observable, oldValue, newValue) -> draw());

        setOnMouseClicked(event -> {
            if (elevationColors == null) return;

            if (event.getButton() == MouseButton.PRIMARY) {
                int row = (int) (event.getY() / cellHeight);
                int column = (int) (event.getX() / cellWidth);
                markerPosition = new Position(row, column);
            } else if (event.getButton() == MouseButton.SECONDARY) {
                markerPosition = null;
            } else {
                return;
            }
            draw();
        });
    }

    private void draw() {
        Platform.runLater(this::realDraw);
    }

    private void realDraw() {
        GraphicsContext g = getGraphicsContext2D();
        g.clearRect(0, 0, getWidth(), getHeight());

        if (elevationColors == null) return;
        cellWidth = getWidth() / elevationColors.get(0).size();
        cellHeight = getHeight() / elevationColors.size();

        // draw grid
        for (int row = 0; row < elevationColors.size(); row++) {
            List<Color> rowColors = elevationColors.get(row);
            for (int column = 0; column < rowColors.size(); column++) {
                Color color = rowColors.get(column);
                g.setFill(color);
                g.fillRect(column * cellWidth, row * cellHeight, cellWidth, cellHeight);
            }
        }

        // draw path
        if (path != null) {
            double pointWidth = cellWidth / 3, pointHeight = cellHeight / 3;
            g.setFill(Color.YELLOW);
            for (Position point : path) {
                g.fillOval(
                        point.getColumn() * cellWidth + cellWidth / 2 - pointWidth / 2,
                        point.getRow() * cellHeight + cellHeight / 2 - pointHeight / 2,
                        pointWidth,
                        pointHeight);
            }
        }

        // draw marker
        if (markerImage != null && markerPosition != null) {
            double markerWidth = 20, markerHeight = 20;
            g.drawImage(markerImage,
                    markerPosition.getColumn() * cellWidth + cellWidth / 2 - markerWidth / 2,
                    markerPosition.getRow() * cellHeight + cellHeight / 2 - markerHeight / 2,
                    markerWidth,
                    markerHeight);
        }

        // draw airplane
        if (planeImage != null && planePosition.get() != null) {
            double planeWidth = 30, planeHeight = 30;

            g.save();
            g.translate(planePosition.get().getColumn() * cellWidth + cellWidth / 2,
                    planePosition.get().getRow() * cellHeight + cellHeight / 2);
            g.rotate(planeHeading.get());
            g.drawImage(planeImage, -planeWidth / 2, -planeHeight / 2, planeWidth, planeHeight);
            g.restore();
        }
    }

    private Color elevationToColor(double elevation, double maxElevation) {
        double normalizedElevation = elevation / maxElevation;
        return Color.hsb(normalizedElevation * 120, 1, 1);
    }

    public Position getPlanePosition() {
        return planePosition.get();
    }

    public ObjectProperty<Position> planePositionProperty() {
        return planePosition;
    }

    public DoubleProperty planeHeadingProperty() {
        return planeHeading;
    }

    public void setElevations(int[][] elevations) {
        OptionalInt max = Arrays.stream(elevations).flatMapToInt(Arrays::stream).max();
        if (!max.isPresent()) return;
        double maxElevation = max.getAsInt();

        elevationColors = Arrays.stream(elevations)
                .map(row -> Arrays.stream(row)
                        .mapToObj(elevation -> elevationToColor(elevation, maxElevation))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
        draw();
    }

    public Position getMarkerPosition() {
        return markerPosition;
    }

    public void setPath(List<Position> path) {
        this.path = path;
        draw();
    }

    public String getPlaneImageFileName() {
        return planeImageFileName.get();
    }

    public void setPlaneImageFileName(String planeImageFileName) {
        this.planeImageFileName.set(planeImageFileName);
        planeImage = new Image(new File(planeImageFileName).toURI().toString());
        draw();
    }

    public String getMarkerImageFileName() {
        return planeImageFileName.get();
    }

    public void setMarkerImageFileName(String markerImageFileName) {
        this.markerImageFileName.set(markerImageFileName);
        markerImage = new Image(new File(markerImageFileName).toURI().toString());
        draw();
    }
}
