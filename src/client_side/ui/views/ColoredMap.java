package client_side.ui.views;

import client_side.ui.Position;
import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
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
        if (elevationColors == null) return;
        cellWidth = getWidth() / elevationColors.get(0).size();
        cellHeight = getHeight() / elevationColors.size();

        GraphicsContext g = getGraphicsContext2D();

        // draw grid
        for (int row = 0; row < elevationColors.size(); row++) {
            List<Color> rowColors = elevationColors.get(row);
            for (int col = 0; col < rowColors.size(); col++) {
                Color color = rowColors.get(col);
                g.setFill(color);
                g.fillRect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
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
            double markerWidth = cellWidth / 2, markerHeight = cellHeight / 2;
            g.drawImage(markerImage,
                    markerPosition.getColumn() * cellWidth + cellWidth / 2 - markerWidth / 2,
                    markerPosition.getRow() * cellHeight + cellHeight / 2 - markerHeight / 2,
                    markerWidth,
                    markerHeight);
        }

        // draw airplane
        if (planeImage != null && planePosition.get() != null) {
            double planeWidth = cellWidth / 1.5, planeHeight = cellHeight / 1.5;

            g.save();
            g.translate(planePosition.get().getColumn() * cellWidth + cellWidth / 2,
                    planePosition.get().getRow() * cellHeight + cellHeight / 2);
            g.rotate(planeHeading.get());
            g.drawImage(planeImage, -planeWidth / 2, -planeHeight / 2, planeWidth, planeHeight);
            g.restore();
        }
    }

    private Color elevationToColor(double elevation, double maxElevation) {
        int normalizedElevation = (int) (elevation * 255 / maxElevation);
        return Color.rgb(255 - normalizedElevation, normalizedElevation, 0);
    }

    public void setPlanePosition(Position planePosition) {
        this.planePosition.set(planePosition);
    }

    public ObjectProperty<Position> planePositionProperty() {
        return planePosition;
    }

    public void setPlaneHeading(double planeHeading) {
        this.planeHeading.set(planeHeading);
    }

    public DoubleProperty planeHeadingProperty() {
        return planeHeading;
    }

    public void setElevations(double[][] elevations) {
        OptionalDouble max = Arrays.stream(elevations).flatMapToDouble(Arrays::stream).max();
        if (!max.isPresent()) return;
        double maxElevation = max.getAsDouble();

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
