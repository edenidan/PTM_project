package client_side.interpreter;

import client_side.Wrapper;
import client_side.interpreter.commands.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter {
    private Lexer lexer = new DefaultLexer();

    private final Map<String, Command> commands = new HashMap<>();
    private final Map<String, Double> symbolTable = new HashMap<>();
    private final Wrapper<Boolean> returned = new Wrapper<>(false);//false until return is called

    public Interpreter() {
        commands.put("openDataServer", new OpenServerCommand());
        commands.put("connect", new ConnectCommand());
        commands.put("disconnect", new DisconnectCommand());
        commands.put("sleep", new SleepCommand(symbolTable));
        commands.put("var", new DefineVarCommand(symbolTable));
        commands.put("=", new AssignmentCommand(symbolTable));
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
            System.out.printf("unhandled exception:\n" +
                            "token number: %s\n" +
                            "error message: %s\n",
                    e.tokenIndex,
                    e.errorMessage);
        }
        return null;
    }

    public int interpret(String[] lines) {
        return interpret(String.join("\n", lines));
    }

    public Interpreter useLexer(Lexer lexer) {
        this.lexer = lexer;
        return this;
    }
}
