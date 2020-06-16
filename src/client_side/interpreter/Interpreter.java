package client_side.interpreter;

import utility.EmptyObservable;
import utility.Wrapper;
import client_side.interpreter.commands.*;

import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;

public class Interpreter {
    private final Lexer lexer = new Lexer();

    private final Map<String, Command> commands = new HashMap<>();

    private final Wrapper<Integer> returnValue = new Wrapper<>(null); // null until return is called
    private final EmptyObservable stopServer = new EmptyObservable();
    private final EmptyObservable stopClient = new EmptyObservable();

    ConcurrentMap<String, Property> properties = new ConcurrentHashMap<>();


    public Interpreter(){
        initInterpreter(null,null);
    }

    public Interpreter(BlockingQueue<String> commandsOutput,BlockingQueue<String> dataInput){
        initInterpreter(commandsOutput,dataInput);
    }


    public void initInterpreter(BlockingQueue<String> commandsOutput,BlockingQueue<String> dataInput) {
        ConcurrentMap<String, Variable> symbolTable = new ConcurrentHashMap<>();

        BlockingQueue<PropertyUpdate> toUpdate = new LinkedBlockingDeque<>();

        commands.put("openDataServer", new OpenServerCommand(stopServer, symbolTable, properties,dataInput));
        commands.put("connect", new ConnectCommand(toUpdate, symbolTable, stopClient,commandsOutput));
        commands.put("disconnect", new DisconnectCommand(stopClient));
        commands.put("sleep", new SleepCommand(symbolTable));
        commands.put("var", new DefineVarCommand(symbolTable));
        commands.put("=", new AssignmentCommand(symbolTable, properties, toUpdate));
        commands.put("if", new IfCommand(symbolTable, commands));
        commands.put("while", new LoopCommand(symbolTable, commands, returnValue));
        commands.put("block", new BlockCommand(commands, returnValue));
        commands.put("return", new ReturnCommand(symbolTable, returnValue));
    }

    public int interpret(String script) {
        List<String> tokens = lexer.lex(script);
        try {
            commands.get("block").doCommand(tokens, 0);
            if (returnValue.get() != null)
                return returnValue.get();
            else
                return 0; // default value

        } catch (CannotInterpretException e) {
            System.out.printf("Syntax error\n" +
                            "token number: %s\n" +
                            "error message: %s\n",
                    e.tokenIndex,
                    e.errorMessage);
        } catch (Exception e) {
            System.out.println("unknown error: " + e.getMessage());
        } finally {
            stopClient.setChangedAndNotify();
            stopServer.setChangedAndNotify();

            // Give time for the threads to close, otherwise the ports may be still taken
            try {
                //Thread.sleep(2000);
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return -1;//error value
    }

    public void abort(){
        returnValue.set(-1);
        stopClient.setChangedAndNotify();
        stopServer.setChangedAndNotify();
    }

    public int interpret(String[] lines) {
        return interpret(String.join("\n", lines));
    }
}
