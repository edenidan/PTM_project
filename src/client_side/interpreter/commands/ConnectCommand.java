package client_side.interpreter.commands;

import client_side.Wrapper;
import client_side.interpreter.*;
import client_side.interpreter.math.ArithmeticParser;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class ConnectCommand implements Command {
    private BlockingQueue<PropertyUpdate> updates;
    private ConcurrentMap<String, Variable> symbolTable;
    private final Wrapper<Boolean> stop;
    private final Thread mainThread;

    public ConnectCommand(BlockingQueue<PropertyUpdate> toUpdate, ConcurrentMap<String, Variable> symbolTable,Wrapper<Boolean> stop) {
        this.updates = toUpdate;
        this.symbolTable = symbolTable;
        this.stop=stop;

        this.mainThread=Thread.currentThread();
    }



    @Override
    public int doCommand(List<String> tokens, int startIndex) throws CannotInterpretException {

        String ip = tokens.get(startIndex + 1);
        double port = ArithmeticParser.calc(tokens, startIndex + 2, symbolTable);
        if (!Classifier.isPort(port) || !Classifier.isAddress(ip))
            throw new CannotInterpretException("ip/port is illegal", startIndex);


        //Connected to server
        this.stop.set(false);
        new Thread(() -> client(ip,(int)port)).start();
        return ArithmeticParser.getEndOfExpression(tokens, startIndex + 2, symbolTable) + 1;
    }

    private void client(String ip, int port) {

        final BufferedWriter w;
        Socket server=null;
        try {
            server = new Socket(ip, (int) port);
            w = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
        } catch (IOException e) {
            mainThread.interrupt();
            return;
        }


        PropertyUpdate update;
        while (this.stop.get()) {//while !stop
            try {
                update = updates.poll(500, TimeUnit.MILLISECONDS);//check stop every 500 millis
                if (update == null)
                    continue;//timeout happened

                w.write(RequestToServer(update));
                w.flush();
            }
            catch (InterruptedException ignored) { }
            catch (IOException e) {
                mainThread.interrupt();
                return;
            }
        }

        disconnect(server,w);
    }

    private void disconnect(Socket server, BufferedWriter w) {
        try {
            w.write("bye\n");
            w.close();
            server.close();
        } catch (IOException ignored) { }
    }

    private String RequestToServer(PropertyUpdate update) {
        return "set " + update.getProperty() + " " + update.getValue() + "\n";
    }

}
