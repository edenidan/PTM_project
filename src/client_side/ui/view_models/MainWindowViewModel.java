package client_side.ui.view_models;

import java.io.IOException;

public interface MainWindowViewModel {
    //sim ip,port
    //searching server ip,port
    void connect(String ip, int port) throws IOException;

    void calculatePath(String ip, int port, int[][] heights, int sourceRow, int sourceColumn, int destinationRow, int destinationColumn);
    //void loadData();

    //script
    void startAutoPilotScript() throws IllegalStateException;

    void stopAutoPilotScript();

    //joystick xy
    //rudder value
    //throttle value
}
