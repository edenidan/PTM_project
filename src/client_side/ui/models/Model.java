package client_side.ui.models;

import javafx.beans.Observable;
import utility.EmptyObservable;

import java.io.IOException;

public interface Model {

    void setThrottle(double throttle)throws IllegalAccessException, InterruptedException;
    void setRudder(double rudder)throws IllegalAccessException, InterruptedException;
    void setElevator(double elevator)throws IllegalAccessException, InterruptedException;
    void setAileron(double aileron)throws IllegalAccessException, InterruptedException;

    void runScript(String script) throws IllegalAccessException;
    void stopScript();

    void calculatePath(String ip, int port, double[][] heights, int sourceRow, int sourceCol, int destRow, int destCol);
    String getPath();
    EmptyObservable getPathDoneObservable();

    void connect(String ip, int port) throws IOException;


}
