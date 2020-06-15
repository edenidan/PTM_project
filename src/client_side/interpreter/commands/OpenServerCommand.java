package client_side.interpreter.commands;

import client_side.interpreter.*;
import client_side.interpreter.math.ArithmeticParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ConcurrentMap;

public class OpenServerCommand implements Command {
    private final ConcurrentMap<String, Variable> symbolTable;
    private final ConcurrentMap<String, Property> properties;
    private Socket dataConnection;

    private Thread serverThread = null;
    private volatile boolean stop = false;

    private final Thread mainThread;

    public OpenServerCommand(Observable stopServer, ConcurrentMap<String, Variable> symbolTable, ConcurrentMap<String, Property> properties, Socket dataConnection) {
        this.symbolTable = symbolTable;
        this.properties = properties;
        this.dataConnection = dataConnection;

        stopServer.addObserver((o, arg) -> stopServerThread());

        this.mainThread = Thread.currentThread();
    }

    private void initPropertiesDefault(){
        this.properties.put("simX", new Property("simX", 0.0));
        this.properties.put("simY", new Property("simY", 0.0));
        this.properties.put("simZ", new Property("simZ", 0.0));
    }

    @Override
    public int doCommand(List<String> tokens, int startIndex) throws CannotInterpretException {
        // blocking call until starting to receive data
        Double port = null;
        if (dataConnection == null) {
            port = ArithmeticParser.calc(tokens, startIndex + 1, symbolTable);
            if (!Classifier.isPort(port))
                throw new CannotInterpretException("illegal port", startIndex);
            //double timesPerSec = ArithmeticParser.calc(tokens,startIndex+2,symbolTable);
        }
        initPropertiesDefault();

        stopServerThread();
        stop = false;
        final Integer finalPort = port.intValue();
        serverThread = new Thread(() -> runServer(finalPort));
        serverThread.start();

        try {
            Thread.sleep(Long.MAX_VALUE);//wait for the first message from the simulator
        } catch (InterruptedException ignored) { }

        if(dataConnection != null)
            return startIndex + 1;
        //else:
        int endOfPortExpression = ArithmeticParser.getEndOfExpression(tokens, startIndex + 1, symbolTable);
        int endOfTimesPerSecExpression = ArithmeticParser.getEndOfExpression(tokens, endOfPortExpression + 1, symbolTable);
        return endOfTimesPerSecExpression + 1;
    }


    private void runServer(Integer port) {
        boolean first = true;
        ServerSocket server = null;
        BufferedReader in = null;
        try {
            if(port != null) {
                server = new ServerSocket(port);
                server.setSoTimeout(1000);
            }

            while (!stop) {
                try {

                    if(port!=null) {
                        Socket client = server.accept();
                        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    }
                    else
                        in = new BufferedReader(new InputStreamReader(dataConnection.getInputStream()));

                    String line;
                    while (!"bye".equals((line = in.readLine())) && line != null) {

                        try {
                            String[] properties = line.split(",");
                            this.properties.get("simX").setValue(Double.parseDouble(properties[0]));
                            this.properties.get("simY").setValue(Double.parseDouble(properties[1]));
                            this.properties.get("simZ").setValue(Double.parseDouble(properties[2]));

                        } catch (NumberFormatException ignored) {
                        }

                        if (first) {
                            first = false;
                            mainThread.interrupt();
                        }
                    }
                } catch (SocketTimeoutException ignored) {
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            mainThread.interrupt();
        }
    }

    private void stopServerThread() {
        stop = true;

        if (serverThread != null) {
            serverThread.interrupt();

        }
    }
}