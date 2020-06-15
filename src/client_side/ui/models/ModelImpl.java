package client_side.ui.models;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;

public class ModelImpl implements Model {

    private final int DATA_SERVER_PORT = 5400;

    private Socket commandsSocket;
    private PrintWriter commandOutput;
    private Socket dataSocket;
    private Reader dataInput;

    public ModelImpl(){

    }

    private void sendSetCommand(String property, double value) {
        commandOutput.println("set "+property+" "+value);
    }

    @Override
    public void setThrottle(double throttle) {
        if(-1 <= throttle && throttle <= 1)
            sendSetCommand("/controls/engines/engine/throttle",throttle);
    }
    @Override
    public void setRudder(double rudder) {
        if(-1 <= rudder && rudder <= 1)
            sendSetCommand("/controls/flight/rudder",rudder);
    }
    @Override
    public void setElevator(double elevator) {
        if(-1 <= elevator && elevator <= 1)
            sendSetCommand("/controls/flight/elevator",elevator);
    }
    @Override
    public void setAileron(double aileron) {
        if(-1 <= aileron && aileron <= 1)
            sendSetCommand("/controls/flight/aileron",aileron);
    }

    @Override
    public void runScript(String script) {

    }

    @Override
    public void stopScript() {

    }

    @Override
    public void calculatePath(String ip, int port, double[][] heights, int destRow, int destCol) {

    }

    @Override
    public void connect(String ip, int port) throws IOException {

        this.commandsSocket = new Socket(ip,port);
        this.commandOutput = new PrintWriter(commandsSocket.getOutputStream());

        ServerSocket dataServer = new ServerSocket(DATA_SERVER_PORT);
        this.dataSocket = dataServer.accept();
        this.dataInput = new InputStreamReader(dataSocket.getInputStream());
    }
}
