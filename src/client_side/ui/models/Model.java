package client_side.ui.models;

import utility.EmptyObservable;

import java.io.IOException;

public interface Model {

    void setThrottle(double throttle)throws IllegalAccessException, InterruptedException;
    void setRudder(double rudder)throws IllegalAccessException, InterruptedException;
    void setElevator(double elevator)throws IllegalAccessException, InterruptedException;
    void setAileron(double aileron)throws IllegalAccessException, InterruptedException;

    void runScript(String script) throws IllegalAccessException;
    void stopScript();

    void calculatePath(String ip, int port, int[][] heights, int sourceRow, int sourceCol, int destRow, int destCol);
    String getPath();
    EmptyObservable getPathDoneObservable();

    double getPlaneX();

    double getPlaneY();

    EmptyObservable getPositionChangedObservable();

    void connect(String ip, int port) throws IOException;


}
