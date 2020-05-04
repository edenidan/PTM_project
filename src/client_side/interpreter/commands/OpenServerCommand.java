package client_side.interpreter.commands;

import client_side.Wrapper;
import client_side.interpreter.*;
import client_side.interpreter.math.ArithmeticParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class OpenServerCommand implements Command {

    private final Wrapper<Boolean> stopServer;
    private final ConcurrentMap<String, Variable> symbolTable;
    private final ConcurrentMap<String, Property> properties;
    private final Thread mainThread;

    public OpenServerCommand(Wrapper<Boolean> stopServer, ConcurrentMap<String, Variable> symbolTable, ConcurrentMap<String, Property> properties) {
        this.stopServer=stopServer;
        this.symbolTable=symbolTable;
        this.properties=properties;
        this.mainThread=Thread.currentThread();
    }

    @Override
    public int doCommand(List<String> tokens, int startIndex)throws CannotInterpretException {
        // blocking call until starting to receive data
        double port = ArithmeticParser.calc(tokens,startIndex+1,symbolTable);
        if(!Classifier.isPort(port))
            throw new CannotInterpretException("illegal port",startIndex);
        //double timesPerSec = ArithmeticParser.calc(tokens,startIndex+2,symbolTable);

        this.properties.put("simX",new Property("simX",0.0));
        this.properties.put("simY",new Property("simY",0.0));
        this.properties.put("simZ",new Property("simZ",0.0));

        new Thread(()->runServer((int)port)).start();
        try {
            Thread.sleep(Long.MAX_VALUE);//wait for the first message from the simulator
        } catch (InterruptedException e) { }

        return startIndex+3;
    }

    private void runServer(int port){
        boolean first=true;
        try {
            ServerSocket server = new ServerSocket(port);
            server.setSoTimeout(1000);
            while (!stopServer.get()) {
                try {
                    Socket client = server.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String line;
                    while (!"bye".equals((line = in.readLine())) && line!=null) {
                        try {
                            String[] properties = line.split(",");
                            this.properties.get("simX").setValue(Double.parseDouble(properties[0]));
                            this.properties.get("simY").setValue(Double.parseDouble(properties[1]));
                            this.properties.get("simZ").setValue(Double.parseDouble(properties[2]));

                        } catch (NumberFormatException e) { }

                        if(first){
                            first=false;
                            mainThread.interrupt();
                        }
                    }
                    in.close();
                    client.close();
                } catch (SocketTimeoutException e) {
                    mainThread.interrupt();
                }
            }
            server.close();
        } catch (IOException e) {
            mainThread.interrupt();
        }
    }
}