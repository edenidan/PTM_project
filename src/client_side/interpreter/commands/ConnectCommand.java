package client_side.interpreter.commands;

import client_side.interpreter.*;
import client_side.interpreter.math.ArithmeticParser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

public class ConnectCommand implements Command {
    private final BlockingQueue<PropertyUpdate> updates;
    private final ConcurrentMap<String, Variable> symbolTable;
    private Socket commandsConnection;

    private Thread clientThread = null;
    private volatile boolean stop = false;

    private final Thread mainThread;

    public ConnectCommand(BlockingQueue<PropertyUpdate> toUpdate, ConcurrentMap<String, Variable> symbolTable, Observable stopClient, Socket commandsConnection) {
        this.updates = toUpdate;
        this.symbolTable = symbolTable;
        this.commandsConnection = commandsConnection;

        stopClient.addObserver((o, arg) -> stopClientThread());

        this.mainThread = Thread.currentThread();
    }


    @Override
    public int doCommand(List<String> tokens, int startIndex) throws CannotInterpretException {
        String ip = null;
        Double port = null;
        if(this.commandsConnection == null) {
            ip = tokens.get(startIndex + 1);
            port = ArithmeticParser.calc(tokens, startIndex + 2, symbolTable);

            if (!Classifier.isAddress(ip))
                throw new CannotInterpretException("ip is illegal", startIndex + 1);
            if (!Classifier.isPort(port))
                throw new CannotInterpretException("port is illegal", startIndex + 2);
        }
        stopClientThread();
        stop = false;

        final String finalIp = ip;
        final Integer finalPort = port.intValue();
        clientThread = new Thread(() -> client(finalIp, finalPort));
        clientThread.start();

        if(this.commandsConnection == null)
            return ArithmeticParser.getEndOfExpression(tokens, startIndex + 2, symbolTable) + 1;
        return startIndex + 1;//if the commands connection is given then the ip,port are not given
    }

    private Writer connectAndGetWriter(String ip,Integer port) throws IOException {
        if(ip == null && port == null)
            return new BufferedWriter(new OutputStreamWriter(this.commandsConnection.getOutputStream()));
        Socket server = new Socket(ip, port);
        return new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
    }

    private void client(String ip, Integer port) {
        // connect to the server
        Writer writer=null;
        try {
            writer = connectAndGetWriter(ip,port);
            // send set messages every time a property is updated
            while (!stop) {
                PropertyUpdate update;
                try {
                    update = updates.take();
                } catch (InterruptedException ignored) {
                    continue;
                }

                writer.write(getSetString(update));
                writer.flush();
            }

            writer.write("bye\n");
            writer.flush();



        } catch (IOException e) {
            mainThread.interrupt();
        }
        finally {
            if(this.commandsConnection == null) {
                try {
                    writer.close();
                } catch (IOException e) { }
            }
        }
    }

    private String getSetString(PropertyUpdate update) {
        return "set " + update.getProperty() + " " + update.getValue() + "\n";
    }

    private void stopClientThread() {
        stop = true;

        if (clientThread != null)
            clientThread.interrupt();
    }
}
