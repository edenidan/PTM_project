package client_side.ui.models;

import client_side.interpreter.Interpreter;
import client_side.interpreter.Property;
import utility.EmptyObservable;

import javax.jnlp.UnavailableServiceException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

public class ModelImpl implements Model  {

    private final int DATA_SERVER_PORT = 5400;

    private boolean autopilotRunning = false;

    MultiOutputsBufferedReader dataInput = null;
    MultiSourcePrintWriter commandOutput = null;

    BlockingQueue<String> commandOutputQ;
    BlockingQueue<String> dataInputQ;

    private Interpreter interpreter = null;

    public ModelImpl() throws IOException {
        ServerSocket dataServer = new ServerSocket(DATA_SERVER_PORT);
        Socket dataSocket = dataServer.accept();
        this.dataInput =new MultiOutputsBufferedReader(new BufferedReader(new InputStreamReader(dataSocket.getInputStream())));

        //TODO: new thread to read from a data input q and send position




    }

    private void sendSetCommand(String property, double value) throws IllegalAccessException, InterruptedException {
        if(commandOutput == null)
            throw new IllegalAccessException("cannot send a command to the simulator without a working connection");
        commandOutputQ.put("set " + property + " " + value + "\n");
    }

    @Override
    public void setThrottle(double throttle) throws IllegalAccessException, InterruptedException{
        if (-1 <= throttle && throttle <= 1)
            sendSetCommand("/controls/engines/engine/throttle", throttle);
    }

    @Override
    public void setRudder(double rudder)throws IllegalAccessException, InterruptedException {
        if (-1 <= rudder && rudder <= 1)
            sendSetCommand("/controls/flight/rudder", rudder);
    }

    @Override
    public void setElevator(double elevator)throws IllegalAccessException, InterruptedException {
        if (-1 <= elevator && elevator <= 1)
            sendSetCommand("/controls/flight/elevator", elevator);
    }

    @Override
    public void setAileron(double aileron)throws IllegalAccessException, InterruptedException {
        if (-1 <= aileron && aileron <= 1)
            sendSetCommand("/controls/flight/aileron", aileron);
    }

    @Override
    public void runScript(String script) throws IllegalAccessException {
        if (autopilotRunning)
            throw new IllegalAccessException("an autopilot script is already running.");
        autopilotRunning = true;
        interpreter.interpret("connect openDataServer "+script);

    }

    @Override
    public void stopScript() {
        if (autopilotRunning)
            interpreter.abort();
        autopilotRunning = false;
    }

    String pathCalculated=null;
    EmptyObservable pathReadyObservable = new EmptyObservable();
    @Override
    public void calculatePath(String ip, int port, double[][] heights, int sourceRow, int sourceCol, int destRow, int destCol) {

        new Thread(() -> {
            try {
                Socket conn = new Socket(ip, port);
                PrintWriter out = new PrintWriter(new BufferedOutputStream(conn.getOutputStream()));
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                for (int i = 0; i < heights.length; i++)
                    out.println(
                            String.join(",", Arrays.stream(heights[i]).mapToObj(Double::toString).toArray(String[]::new))
                    );
                out.println(sourceRow + "," + sourceCol);
                out.println(destRow + "," + destCol);

                this.pathCalculated = in.readLine();
            }
            catch (IOException e){
                this.pathCalculated=null;
            }
            pathReadyObservable.setChangedAndNotify();
        }).start();
    }

    @Override
    public String getPath() {
        return pathCalculated;
    }

    @Override
    public EmptyObservable getPathDoneObservable() {
        return this.pathReadyObservable;

    }

    @Override
    public void connect(String ip, int port) throws IOException {

        Socket commandsSocket = new Socket(ip, port);
        this.commandOutput =new MultiSourcePrintWriter(new PrintWriter(commandsSocket.getOutputStream()));



        interpreter = new Interpreter(commandOutput.getInputChannel(), dataInput.getOutputChannel());
    }
}
