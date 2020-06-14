package client_side.ui.models;

import client_side.interpreter.Interpreter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ModelImpl implements Model {

    private final int DATA_SERVER_PORT = 5400;

    private boolean autopilotRunning = false;

    private Socket commandsSocket = null;
    private PrintWriter commandOutput = null;
    private Socket dataSocket = null;
    private Reader dataInput = null;

    private Interpreter interpreter = null;

    public ModelImpl() {

    }

    private void sendSetCommand(String property, double value) {
        commandOutput.println("set " + property + " " + value);
    }

    @Override
    public void setThrottle(double throttle) {
        if (-1 <= throttle && throttle <= 1)
            sendSetCommand("/controls/engines/engine/throttle", throttle);
    }

    @Override
    public void setRudder(double rudder) {
        if (-1 <= rudder && rudder <= 1)
            sendSetCommand("/controls/flight/rudder", rudder);
    }

    @Override
    public void setElevator(double elevator) {
        if (-1 <= elevator && elevator <= 1)
            sendSetCommand("/controls/flight/elevator", elevator);
    }

    @Override
    public void setAileron(double aileron) {
        if (-1 <= aileron && aileron <= 1)
            sendSetCommand("/controls/flight/aileron", aileron);
    }

    @Override
    public void runScript(String script) throws IllegalAccessException {
        if (autopilotRunning)
            throw new IllegalAccessException("an autopilot script is already running.");
        autopilotRunning = true;
        interpreter.interpret(script);
    }

    @Override
    public void stopScript() {
        if (autopilotRunning)
            interpreter.abort();
        autopilotRunning = false;
    }


    @Override
    public String calculatePath(String ip, int port, double[][] heights, int sourceRow, int sourceCol, int destRow, int destCol) throws IOException {
        try (Socket conn = new Socket(ip, port);
             PrintWriter out = new PrintWriter(new BufferedOutputStream(conn.getOutputStream()));
             BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {

            for (double[] height : heights)
                out.println(Arrays.stream(height).mapToObj(Double::toString).collect(Collectors.joining(",")));
            out.println(sourceRow + "," + sourceCol);
            out.println(destRow + "," + destCol);

            return in.readLine();
        }
    }

    @Override
    public void connect(String ip, int port) throws IOException {

        this.commandsSocket = new Socket(ip, port);
        this.commandOutput = new PrintWriter(commandsSocket.getOutputStream());

        ServerSocket dataServer = new ServerSocket(DATA_SERVER_PORT);
        this.dataSocket = dataServer.accept();
        this.dataInput = new InputStreamReader(dataSocket.getInputStream());

        interpreter = new Interpreter(commandsSocket, dataSocket);
    }
}
