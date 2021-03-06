package client_side.ui.models;

import client_side.interpreter.Interpreter;
import client_side.ui.Coordinate;
import sun.plugin.dom.exception.WrongDocumentException;
import utility.EmptyObservable;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

public class ModelImpl implements Model {

    private final int DATA_SERVER_PORT = 5400;

    private boolean autopilotRunning = false;

    MultiOutputsBufferedReader dataInput;
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
    public void runScript(String script) throws IllegalStateException {
        if (autopilotRunning)
            throw new IllegalStateException("an autopilot script is already running.");
        autopilotRunning = true;
        BlockingQueue<String> dataInputQInterpreter = this.dataInput.getOutputChannel();

        new Thread(() -> {
            interpreter = new Interpreter(commandOutput.getInputChannel(), dataInputQInterpreter);
            interpreter.interpret("connect openDataServer " + script);
            this.dataInput.unsubscribe(dataInputQInterpreter);
            autopilotRunning = false;
        }).start();
    }

    @Override
    public void stopScript() {
        if (autopilotRunning)
            interpreter.abort();
        autopilotRunning = false;
    }

    String pathCalculated = null;
    EmptyObservable pathReadyObservable = new EmptyObservable();

    private int[][] minimizeMat(int[][] mat) {

        int[][] res = new int[mat.length][mat[0].length];

        for (int i = 0; i < res.length; i++)
            for (int j = 0; j < res[0].length; j++) {
                res[i][j] = (int) (0.02 * mat[i][j]);
                res[i][j]++;
            }
        return res;
    }

    @Override
    public void calculatePath(String ip, int port, int[][] heights, int sourceRow, int sourceColumn, int destinationRow, int destinationColumn) {

        int[][] _heights = minimizeMat(heights);
        int _sourceRow = sourceRow;///5;
        int _sourceColumn = sourceColumn;///5;

        int _destinationRow = destinationRow;///5;
        int _destinationColumn = destinationColumn;///5;

        new Thread(() -> {
            try {
                Socket conn = new Socket(ip, port);
                PrintWriter out = new PrintWriter(new BufferedOutputStream(conn.getOutputStream()));
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                for (int[] height : _heights)
                    out.println(Arrays.stream(height).mapToObj(a -> Integer.toString(a + 1)).collect(Collectors.joining(",")));
                out.println("end");
                out.println(_sourceRow + "," + _sourceColumn);
                out.println(_destinationRow + "," + _destinationColumn);
                out.flush();

                String path = in.readLine();
                this.pathCalculated = path;//.replaceAll("Up|Down|Left|Right","$0,$0,$0,$0,$0");
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


    private Coordinate planeCoordinate = null;
    private final EmptyObservable CoordinateChanged = new EmptyObservable();
    private Double heading = null;
    private final EmptyObservable headingChanged = new EmptyObservable();


    @Override
    public Coordinate getPlaneCoordinate() {
        return this.planeCoordinate;
    }

    @Override
    public EmptyObservable getPositionChangedObservable() {
        return this.CoordinateChanged;
    }

    @Override
    public EmptyObservable getHeadingChangedObservable() {
        return this.headingChanged;
    }

    @Override
    public Double getHeading() {
        return heading;
    }

    @Override
    public void connect(String ip, int port) throws IOException {

        Socket commandsSocket = new Socket(ip, port);
        this.commandOutput = new MultiSourcePrintWriter(new PrintWriter(commandsSocket.getOutputStream()));
        this.commandOutputQ = commandOutput.getInputChannel();

        Socket positionClient = new Socket(ip, port);

        BlockingQueue<String> commandInputQ;

        InputStream positionInput = positionClient.getInputStream();
        BufferedReader positionReader = new BufferedReader(new InputStreamReader(positionInput));
        PrintWriter positionOutput = new PrintWriter(positionClient.getOutputStream());

        new Thread(() -> {
            while (true) {
                try {
                    positionOutput.println("dump /position");
                    positionOutput.flush();

                    byte[] data = new byte[1024];
                    positionInput.read(data, 0, 1024);
                    String dataXml = new String(data, StandardCharsets.UTF_8);

                    this.planeCoordinate = new Coordinate(positionXmlToPlaneLatitude(dataXml), positionXmlToPlaneLongitude(dataXml));
                    this.CoordinateChanged.setChangedAndNotify();


                    positionOutput.println("get /instrumentation/heading-indicator/indicated-heading-deg");
                    positionOutput.flush();
                    String headingResponse = positionReader.readLine();

                    this.heading = GetHeadingFromResponse(headingResponse);
                    System.out.println(heading);
                    this.headingChanged.setChangedAndNotify();

                } catch (IOException | WrongDocumentException ignored) {
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
        }).start();
    }

    private Double GetHeadingFromResponse(String headingResponse) {
        return Double.parseDouble(headingResponse.split("'")[1]);
    }

    private double positionXmlToPlaneLatitude(String positionXml) throws WrongDocumentException {
        return getValueByTag(positionXml, "<longitude-deg type=\"double\">");
    }

    private double positionXmlToPlaneLongitude(String positionXml) throws WrongDocumentException {
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
        int endIndex = xml.substring(startIndex).indexOf("<") - 1 + startIndex;

        String res = xml.substring(startIndex, endIndex);
        return Double.parseDouble(res);
    }
}
