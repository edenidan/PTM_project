package client_side.ui.models;

import client_side.ui.Coordinate;
import utility.EmptyObservable;

import java.io.IOException;

public interface Model {

    void setThrottle(double throttle)throws IllegalAccessException, InterruptedException;
    void setRudder(double rudder)throws IllegalAccessException, InterruptedException;
    void setElevator(double elevator)throws IllegalAccessException, InterruptedException;
    void setAileron(double aileron)throws IllegalAccessException, InterruptedException;

    void runScript(String script) throws IllegalStateException;
    void stopScript();

    void calculatePath(String ip, int port, int[][] heights, int sourceRow, int sourceColumn, int destinationRow, int destinationColumn);
    String getPath();
    EmptyObservable getPathDoneObservable();

    Coordinate getPlaneCoordinate();

    EmptyObservable getPositionChangedObservable();

    EmptyObservable getHeadingChangedObservable();

    Double getHeading();

    void connect(String ip, int port) throws IOException;


}
