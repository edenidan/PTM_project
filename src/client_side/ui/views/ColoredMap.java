package client_side.ui.views;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

public class ColoredMap extends Canvas {
    private int planeRow, planeColumn;
    private double planeAngle;

    private List<List<Color>> elevationColors;

    private Integer markerRow, markerColumn;

    private List<PathPoint> path;

    private final StringProperty planeImageFileName = new SimpleStringProperty();
    private Image planeImage;
    private final StringProperty markerImageFileName = new SimpleStringProperty();
    private Image markerImage;

    private void draw() {
        if (elevationColors == null) return;
        double cellWidth = getWidth() / elevationColors.get(0).size();
        double cellHeight = getHeight() / elevationColors.size();

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
            for (PathPoint point : path) {
                g.fillOval(
                        point.column * cellWidth + cellWidth / 2 - pointWidth / 2,
                        point.row * cellHeight + cellHeight / 2 - pointHeight / 2,
                        pointWidth,
                        pointHeight);
            }
        }

        // draw marker
        if (markerImage != null && markerRow != null && markerColumn != null) {
            double markerWidth = cellWidth / 2, markerHeight = cellHeight / 2;
            g.drawImage(markerImage,
                    markerColumn * cellWidth + cellWidth / 2 - markerWidth / 2,
                    markerRow * cellHeight + cellHeight / 2 - markerHeight / 2,
                    markerWidth,
                    markerHeight);
        }

        // draw airplane
        if (planeImage != null) {
            double planeWidth = cellWidth / 1.5, planeHeight = cellHeight / 1.5;

            g.save();
            g.translate(planeColumn * cellWidth + cellWidth / 2,
                    planeRow * cellHeight + cellHeight / 2);
            g.rotate(planeAngle);
            g.drawImage(planeImage, -planeWidth / 2, -planeHeight / 2, planeWidth, planeHeight);
            g.restore();
        }
    }

    private Color elevationToColor(double elevation, double maxElevation) {
        int normalizedElevation = (int) (elevation * 255 / maxElevation);
        return Color.rgb(255 - normalizedElevation, normalizedElevation, 0);
    }

    public void setPlanePosition(int planeRow, int planeColumn) {
        this.planeRow = planeRow;
        this.planeColumn = planeColumn;
        draw();
    }

    public void setPlaneAngle(double planeAngle) {
        this.planeAngle = planeAngle;
        draw();
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

    public Integer getMarkerRow() {
        return markerRow;
    }

    public Integer getMarkerColumn() {
        return markerColumn;
    }

    public void setPath(List<PathPoint> path) {
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

    public static class PathPoint {
        private final int row, column;

        public PathPoint(int row, int column) {
            this.row = row;
            this.column = column;
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }
    }
}
