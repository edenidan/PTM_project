package client_side.ui.views;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class ColoredMap extends Canvas {
    int planeRow, planeColumn;

    double planeAngle;
    double[][] elevations;

    public int getPlaneRow() {
        return planeRow;
    }

    public int getPlaneColumn() {
        return planeColumn;
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
        this.elevations = elevations;
        draw();
    }

    private void draw() {
        if (elevations == null) return;
        
        GraphicsContext g = getGraphicsContext2D();
    }
}
