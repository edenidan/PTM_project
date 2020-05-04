package client_side.interpreter;

import client_side.Wrapper;
import client_side.interpreter.commands.*;

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

    private final Wrapper<Boolean> returned = new Wrapper<>(false);//false until return is called
    private final EmptyObservable stopServer = new EmptyObservable();
    private final EmptyObservable stopClient = new EmptyObservable();

    public Interpreter() {
        ConcurrentMap<String, Variable> symbolTable = new ConcurrentHashMap<>();
        ConcurrentMap<String, Property> properties = new ConcurrentHashMap<>();

        BlockingQueue<PropertyUpdate> toUpdate = new LinkedBlockingDeque<>();

        commands.put("openDataServer", new OpenServerCommand(stopServer, symbolTable, properties));
        commands.put("connect", new ConnectCommand(toUpdate, symbolTable, stopClient));
        commands.put("disconnect", new DisconnectCommand(stopClient));
        commands.put("sleep", new SleepCommand(symbolTable));
        commands.put("var", new DefineVarCommand(symbolTable));
        commands.put("=", new AssignmentCommand(symbolTable, properties, toUpdate));
        commands.put("if", new IfCommand(symbolTable, commands));
        commands.put("while", new LoopCommand(symbolTable, commands, returned));
        commands.put("block", new BlockCommand(commands, returned));
        commands.put("return", new ReturnCommand(symbolTable, returned));
    }

    public Integer interpret(String script) {
        List<String> tokens = lexer.lex(script);
        try {
            int retVal = commands.get("block").doCommand(tokens, 0);
            if (returned.get())
                return retVal;
            return 0;//default value

        } catch (CannotInterpretException e) {
            System.out.printf("Syntax error\n" +
                            "token number: %s\n" +
                            "error message: %s\n",
                    e.tokenIndex,
                    e.errorMessage);
        } finally {
            stopClient.setChangedAndNotify();
            stopServer.setChangedAndNotify();

            // Give time for the threads to close, otherwise the ports may be still taken
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return -1;//error value
    }

    public int interpret(String[] lines) {
        return interpret(String.join("\n", lines));
    }
}
