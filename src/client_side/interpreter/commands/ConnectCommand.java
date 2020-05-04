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

    private Thread clientThread = null;
    private volatile boolean stop = false;

    private final Thread mainThread;

    public ConnectCommand(BlockingQueue<PropertyUpdate> toUpdate, ConcurrentMap<String, Variable> symbolTable, Observable stopClient) {
        this.updates = toUpdate;
        this.symbolTable = symbolTable;

        stopClient.addObserver((o, arg) -> stopClientThread());

        this.mainThread = Thread.currentThread();
    }


    @Override
    public int doCommand(List<String> tokens, int startIndex) throws CannotInterpretException {
        String ip = tokens.get(startIndex + 1);
        double port = ArithmeticParser.calc(tokens, startIndex + 2, symbolTable);

        if (!Classifier.isAddress(ip))
            throw new CannotInterpretException("ip is illegal", startIndex + 1);
        if (!Classifier.isPort(port))
            throw new CannotInterpretException("port is illegal", startIndex + 2);

        stopClientThread();

        stop = false;
        clientThread = new Thread(() -> client(ip, (int) port));
        clientThread.start();

        return ArithmeticParser.getEndOfExpression(tokens, startIndex + 2, symbolTable) + 1;
    }

    private void client(String ip, int port) {
        // connect to the server
        try (Socket server = new Socket(ip, port);
             Writer writer = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()))) {

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
