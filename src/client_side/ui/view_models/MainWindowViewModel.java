package client_side.ui.view_models;

public interface MainWindowViewModel {


    //sim ip,port
    //searching server ip,port
    void connect(String ip,int port);
    void calculatePath(String ip,int port);
    //void loadData();

    //script
    void startAutoPilotScript();
    void stopAutoPilotScript();


    //joystick xy
    //rudder value
    //throttle value

}
