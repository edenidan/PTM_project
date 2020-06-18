package client_side.interpreter.commands;

import client_side.interpreter.*;
import client_side.interpreter.math.ArithmeticParser;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

public class OpenServerCommand implements Command {
    private final ConcurrentMap<String, Variable> symbolTable;
    private final ConcurrentMap<String, Property> properties;
    private final BlockingQueue<String> dataInput;

    private Thread serverThread = null;
    private volatile boolean stop = false;

    private final Thread mainThread;
    private final List<String> propertyNames;

    public OpenServerCommand(Observable stopServer, ConcurrentMap<String, Variable> symbolTable, ConcurrentMap<String, Property> properties, BlockingQueue<String> dataInput) {
        this.symbolTable = symbolTable;
        this.properties = properties;
        this.dataInput = dataInput;

        stopServer.addObserver((o, arg) -> stopServerThread());

        this.mainThread = Thread.currentThread();

        propertyNames = parseGenericSmallXML();
    }

    private void initPropertiesDefault() {
        for (String name : propertyNames)
            properties.put(name, new Property(name, 0));
//        this.properties.put("simX", new Property("simX", 0.0));
//        this.properties.put("simY", new Property("simY", 0.0));
//        this.properties.put("simZ", new Property("simZ", 0.0));
    }

    private List<String> parseGenericSmallXML() {
        List<String> propertyNames = new ArrayList<>();
        String genericSmallFile = "resources/generic_small.xml";
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(genericSmallFile));

            NodeList names = document.getElementsByTagName("name");
            for (int i = 0; i < names.getLength(); i++) {
                propertyNames.add(names.item(i).getTextContent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return propertyNames;
    }

    @Override
    public int doCommand(List<String> tokens, int startIndex) throws CannotInterpretException {
        // blocking call until starting to receive data
        Double port = null;
        if (dataInput == null) {
            port = ArithmeticParser.calc(tokens, startIndex + 1, symbolTable);
            if (!Classifier.isPort(port))
                throw new CannotInterpretException("illegal port", startIndex);
            //double timesPerSec = ArithmeticParser.calc(tokens,startIndex+2,symbolTable);
        }
        initPropertiesDefault();

        stopServerThread();
        stop = false;
        final Integer finalPort = port != null ? port.intValue() : null;
        serverThread = new Thread(() -> runServer(finalPort));
        serverThread.start();

        try {
            Thread.sleep(Long.MAX_VALUE);//wait for the first message from the simulator
        } catch (InterruptedException ignored) {
        }

        if (dataInput != null)
            return startIndex + 1;
        //else:
        int endOfPortExpression = ArithmeticParser.getEndOfExpression(tokens, startIndex + 1, symbolTable);
        int endOfTimesPerSecExpression = ArithmeticParser.getEndOfExpression(tokens, endOfPortExpression + 1, symbolTable);
        return endOfTimesPerSecExpression + 1;
    }


    private void runServer(Integer port) {
        try {
            Thread.sleep(1);
        } catch (InterruptedException ignored) {
        }
        boolean first = true;
        ServerSocket server = null;
        BufferedReader in = null;
        try {
            if (port != null) {
                server = new ServerSocket(port);
                server.setSoTimeout(1000);
            }

            while (!stop) {
                try {

                    if (port != null) {
                        Socket client = server.accept();
                        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    }

                    String line;
                    while (true) {

                        if (this.dataInput == null)
                            line = in.readLine();
                        else
                            line = this.dataInput.take();

                        if (line == null || "bye".equals(line))
                            break;

                        try {
                            String[] properties = line.split(",");
                            for (int i = 0; i < this.propertyNames.size(); i++)
                                this.properties.get(propertyNames.get(i)).setValue(Double.parseDouble(properties[i]));
//                            this.properties.get("simX").setValue(Double.parseDouble(properties[0]));
//                            this.properties.get("simY").setValue(Double.parseDouble(properties[1]));
//                            this.properties.get("simZ").setValue(Double.parseDouble(properties[2]));

                        } catch (NumberFormatException ignored) {
                        } finally {
                            if (this.dataInput == null)
                                in.close();
                        }

                        if (first) {
                            first = false;
                            mainThread.interrupt();
                        }
                    }
                } catch (SocketTimeoutException | InterruptedException ignored) {
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            mainThread.interrupt();
        } finally {
            if (this.dataInput == null)
                try {
                    server.close();
                } catch (IOException ignored) {
                }
        }
    }

    private void stopServerThread() {
        stop = true;

        if (serverThread != null) {
            serverThread.interrupt();
            try {
                serverThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}