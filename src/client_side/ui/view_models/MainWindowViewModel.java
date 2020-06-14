package client_side.ui.view_models;

import java.io.IOException;

public interface MainWindowViewModel {
    //sim ip,port
    //searching server ip,port
    void connect(String ip,int port) throws IOException;
    void calculatePath(String ip, int port, double[][] heights, int sourceRow, int sourceCol, int destRow, int destCol);
    //void loadData();

    //script
    void startAutoPilotScript() throws IllegalAccessException;
    void stopAutoPilotScript();


    //joystick xy
    //rudder value
    //throttle value

}
