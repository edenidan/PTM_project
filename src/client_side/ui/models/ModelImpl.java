package client_side.ui.models;

import client_side.interpreter.Interpreter;

import javafx.beans.property.DoubleProperty;
import sun.plugin.dom.exception.WrongDocumentException;
import utility.EmptyObservable;


import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import java.util.concurrent.BlockingQueue;

public class ModelImpl implements Model {

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
        this.dataInput = new MultiOutputsBufferedReader(new BufferedReader(new InputStreamReader(dataSocket.getInputStream())));

        dataInputQ = dataInput.getOutputChannel();
        /*new Thread(() -> {
            while (true) {
                try {
                    String data = dataInputQ.take();
                    System.out.println(data);
                    //TO DO: set this.planeX and this.PlaneY
                    this.positionChanged.setChangedAndNotify();
                } catch (InterruptedException ignored) {
                }
            }
        }).start();*/


    }

    private void sendSetCommand(String property, double value) throws IllegalAccessException, InterruptedException {
        if (commandOutput == null)
            throw new IllegalAccessException("cannot send a command to the simulator without a working connection");
        commandOutputQ.put("set " + property + " " + value);
    }

    @Override
    public void setThrottle(double throttle) throws IllegalAccessException, InterruptedException {
        if (-1 <= throttle && throttle <= 1)
            sendSetCommand("/controls/engines/current-engine/throttle", throttle);
    }

    @Override
    public void setRudder(double rudder) throws IllegalAccessException, InterruptedException {
        if (-1 <= rudder && rudder <= 1)
            sendSetCommand("/controls/flight/rudder", rudder);
    }

    @Override
    public void setElevator(double elevator) throws IllegalAccessException, InterruptedException {
        if (-1 <= elevator && elevator <= 1)
            sendSetCommand("/controls/flight/elevator", elevator);
    }

    @Override
    public void setAileron(double aileron) throws IllegalAccessException, InterruptedException {
        if (-1 <= aileron && aileron <= 1)
            sendSetCommand("/controls/flight/aileron", aileron);
    }

    @Override
    public void runScript(String script) throws IllegalAccessException {
        if (autopilotRunning)
            throw new IllegalAccessException("an autopilot script is already running.");
        autopilotRunning = true;
        BlockingQueue<String> dataInputQInterpreter = this.dataInput.getOutputChannel();
        interpreter = new Interpreter(commandOutput.getInputChannel(), dataInputQInterpreter);
        interpreter.interpret("connect openDataServer " + script);
        this.dataInput.unsubscribe(dataInputQInterpreter);
    }

    @Override
    public void stopScript() {
        if (autopilotRunning)
            interpreter.abort();
        autopilotRunning = false;
    }

    String pathCalculated = null;
    EmptyObservable pathReadyObservable = new EmptyObservable();

    @Override
    public void calculatePath(String ip, int port, double[][] heights, int sourceRow, int sourceCol, int destRow, int destCol) {

        new Thread(() -> {
            try {
                Socket conn = new Socket(ip, port);
                PrintWriter out = new PrintWriter(new BufferedOutputStream(conn.getOutputStream()));
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                for (double[] height : heights)
                    out.println(
                            String.join(",", Arrays.stream(height).mapToObj(Double::toString).toArray(String[]::new))
                    );
                out.println(sourceRow + "," + sourceCol);
                out.println(destRow + "," + destCol);

                this.pathCalculated = in.readLine();
            } catch (IOException e) {
                this.pathCalculated = null;
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

    private double planeX = 0;
    private double planeY = 0;
    private final EmptyObservable positionChanged = new EmptyObservable();

    @Override
    public double getPlaneX() {
        return planeX;
    }

    @Override
    public double getPlaneY() {
        return planeY;
    }

    @Override
    public EmptyObservable getPositionChangedObservable() {
        return this.positionChanged;
    }

    @Override
    public void connect(String ip, int port) throws IOException {

        Socket commandsSocket = new Socket(ip, port);
        this.commandOutput = new MultiSourcePrintWriter(new PrintWriter(commandsSocket.getOutputStream()));
        this.commandOutputQ = commandOutput.getInputChannel();

        Socket positionClient = new Socket(ip, port);

        BlockingQueue<String> commandInputQ;

        InputStream positionInput = positionClient.getInputStream();
        PrintWriter positionOutput = new PrintWriter(positionClient.getOutputStream());

        new Thread(() -> {

            while (true) {

                try {

                    positionOutput.println("dump /position");
                    positionOutput.flush();

                    byte[] data = new byte[1024];
                    positionInput.read(data, 0, 1024);
                    String dataXml = new String(data, StandardCharsets.UTF_8);

                    System.out.println(positionXmlToPlaneX(dataXml) + " " +positionXmlToPlaneY(dataXml));
                    //todo: set this.planeX and this.PlaneY
                    //see more info: http://wiki.flightgear.org/Telnet_usage#dump

                    this.positionChanged.setChangedAndNotify();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
        }).start();
    }

    private double positionXmlToPlaneX(String positionXml) throws WrongDocumentException {
        return getValueByTag(positionXml, "<longitude-deg type=\"double\">");
    }

    private double positionXmlToPlaneY(String positionXml) throws WrongDocumentException {
        return getValueByTag(positionXml, "<latitude-deg type=\"double\">");
    }

    private double getValueByTag(String xml, String tag) throws WrongDocumentException {

        if (!tag.startsWith("<"))
            tag = "<" + tag;

        if (!tag.endsWith(">"))
            tag = tag + ">";

        if (!xml.contains(tag))
            throw new WrongDocumentException("invalid or wrong xml document");

        int startIndex = xml.indexOf(tag) + tag.length();
        int endIndex = xml.substring(startIndex).indexOf("<") - 1;

        return Double.parseDouble(tag.substring(startIndex, endIndex));
    }
}
