package client_side.interpreter.commands;

import client_side.interpreter.*;
import client_side.interpreter.math.ArithmeticParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ConcurrentMap;

public class OpenServerCommand implements Command {
    private final ConcurrentMap<String, Variable> symbolTable;
    private final ConcurrentMap<String, Property> properties;

    private Thread serverThread = null;
    private volatile boolean stop = false;

    private final Thread mainThread;

    public OpenServerCommand(Observable stopServer, ConcurrentMap<String, Variable> symbolTable, ConcurrentMap<String, Property> properties) {
        this.symbolTable = symbolTable;
        this.properties = properties;

        stopServer.addObserver((o, arg) -> stopServerThread());

        this.mainThread = Thread.currentThread();
    }

    @Override
    public int doCommand(List<String> tokens, int startIndex) throws CannotInterpretException {
        // blocking call until starting to receive data
        double port = ArithmeticParser.calc(tokens, startIndex + 1, symbolTable);
        if (!Classifier.isPort(port))
            throw new CannotInterpretException("illegal port", startIndex);
        //double timesPerSec = ArithmeticParser.calc(tokens,startIndex+2,symbolTable);

        this.properties.put("simX", new Property("simX", 0.0));
        this.properties.put("simY", new Property("simY", 0.0));
        this.properties.put("simZ", new Property("simZ", 0.0));

        stop = false;
        new Thread(() -> runServer((int) port)).start();
        try {
            Thread.sleep(Long.MAX_VALUE);//wait for the first message from the simulator
        } catch (InterruptedException ignored) {
        }

        return startIndex + 3;
    }

//    private Socket currentClientSocket = null;
//
//    private void runServerAndWait(int port, int startIndex) throws CannotInterpretException {
//        ServerSocket server = null;
//        currentClientSocket = null;
//        BufferedReader in = null;
//        try {
//            server = new ServerSocket(port);
//            currentClientSocket = server.accept();
//            in = new BufferedReader(new InputStreamReader(currentClientSocket.getInputStream()));
//
//            String[] propertyValues = in.readLine().split(",");
//            properties.get("simX").setValue(Double.parseDouble(propertyValues[0]));
//            properties.get("simY").setValue(Double.parseDouble(propertyValues[1]));
//            properties.get("simZ").setValue(Double.parseDouble(propertyValues[2]));
//        } catch (IOException e) {
//            try {
//                if (in != null) in.close();
//                if (currentClientSocket != null) currentClientSocket.close();
//                if (server != null) server.close();
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//                throw new CannotInterpretException("Error in starting server", startIndex);
//            }
//
//            e.printStackTrace();
//            throw new CannotInterpretException("Error in starting server", startIndex);
//        }
//
//        BufferedReader finalIn = in;
//        ServerSocket finalServer = server;
//        serverThread = new Thread(() -> {
//            try {
//                String line;
//                while ((line = finalIn.readLine()) != null) {
//                    String[] propertyValuesInThread = line.split(",");
//                    properties.get("simX").setValue(Double.parseDouble(propertyValuesInThread[0]));
//                    properties.get("simY").setValue(Double.parseDouble(propertyValuesInThread[1]));
//                    properties.get("simZ").setValue(Double.parseDouble(propertyValuesInThread[2]));
//                }
//                finalIn.close();
//                this.currentClientSocket.close();
//                this.currentClientSocket = null;
//
//                finalServer.setSoTimeout(500); // check to stop every 500 milliseconds
//                while (!stop) {
//                    BufferedReader inInLoop = null;
//                    try {
//                        this.currentClientSocket = finalServer.accept();
//                        inInLoop = new BufferedReader(new InputStreamReader(this.currentClientSocket.getInputStream()));
//
//                        String lineInLoop;
//                        while ((lineInLoop = inInLoop.readLine()) != null) {
//                            String[] propertyValuesInLoop = lineInLoop.split(",");
//                            properties.get("simX").setValue(Double.parseDouble(propertyValuesInLoop[0]));
//                            properties.get("simY").setValue(Double.parseDouble(propertyValuesInLoop[1]));
//                            properties.get("simZ").setValue(Double.parseDouble(propertyValuesInLoop[2]));
//                        }
//                    } catch (SocketTimeoutException ignored) {
//                    } catch (IOException e) {
//                        try {
//                            if (this.currentClientSocket != null) this.currentClientSocket.close();
//                            if (inInLoop != null) inInLoop.close();
//                        } catch (IOException e2) {
//                            e.printStackTrace();
//                        }
//                        mainThread.interrupt();
//                    }
//                }
//
//                finalServer.close();
//            } catch (IOException ignored) {
//                mainThread.interrupt();
//            }
//        });
//        serverThread.start();
//
//    }

    private void runServer(int port) {
        boolean first = true;
        try (ServerSocket server = new ServerSocket(port)) {
            server.setSoTimeout(1000);
            while (!stop) {
                try (Socket client = server.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
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
//            try {
//                if (currentClientSocket != null)
//                    currentClientSocket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            System.out.println("Stopping the server");
        }
    }
}