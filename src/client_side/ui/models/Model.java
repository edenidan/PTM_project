package client_side.ui.models;

import java.io.IOException;

public interface Model {

    void setThrottle(double throttle);
    void setRudder(double rudder);
    void setElevator(double elevator);
    void setAileron(double aileron);

    void runScript(String script) throws IllegalAccessException;
    void stopScript();

    String calculatePath(String ip, int port, double[][] heights, int sourceRow, int sourceCol, int destRow, int destCol) throws IOException;

    void connect(String ip, int port) throws IOException;
}
