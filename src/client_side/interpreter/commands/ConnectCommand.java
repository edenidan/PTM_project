package client_side.interpreter.commands;

import client_side.interpreter.*;
import client_side.interpreter.math.ArithmeticParser;
import jdk.internal.org.objectweb.asm.util.TraceAnnotationVisitor;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class ConnectCommand implements Command {
    private BlockingQueue<PropertyUpdate> updates;
    private ConcurrentMap<String, Variable> symbolTable;

    public ConnectCommand(BlockingQueue<PropertyUpdate> toUpdate, ConcurrentMap<String, Variable> symbolTable) {
        this.updates = toUpdate;
        this.symbolTable = symbolTable;
    }

    @Override
    public int doCommand(List<String> tokens, int startIndex) throws CannotInterpretException {

        String ip = tokens.get(startIndex + 1);
        double port = ArithmeticParser.calc(tokens, startIndex + 2, symbolTable);
        if(!isPort(port) || !isAddress(ip))
            throw new CannotInterpretException("ip/port is illegal",startIndex);

        final Writer w;
        try {
            Socket serverConn = new Socket(ip,(int)port);
            w = new OutputStreamWriter(new BufferedOutputStream(serverConn.getOutputStream()));
        } catch (IOException e) {
            throw new CannotInterpretException(e.getMessage(),startIndex);
        }
        //Connected to server

        new Thread(() -> dataSender(w)).start();
        return ArithmeticParser.getEndOfExpression(tokens,startIndex+2,symbolTable)+1;
    }

    private void dataSender(Writer out) {

        while (true) {
            PropertyUpdate pu = null;
            try { pu = updates.poll(1, TimeUnit.SECONDS); }
            catch (Exception e) { continue;}
            if(pu==null)
                continue;

            try {
                out.write("set " + pu.getProperty() + " " + pu.getValue());
            } catch (IOException e) {
                //TODO
System.out.println("no send");
            }
        }
    }

    private boolean isPort(double port){
        return true;
    }
    private boolean isAddress(String ip){
        return true;
    }

}
