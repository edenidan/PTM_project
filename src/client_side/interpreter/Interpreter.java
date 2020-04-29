package client_side.interpreter;

import client_side.interpreter.commands.*;

import java.util.HashMap;
import java.util.Map;

public class Interpreter {
    private Lexer lexer = new DefaultLexer();
    private Parser parser = new DefaultParser();

    private Map<String, Command> commands = new HashMap<>();
    private ReturnCommand returnCommand;
    private Map<String, Double> symbolTable = new HashMap<>();

    public Interpreter() {
        returnCommand = new ReturnCommand(symbolTable);
        commands.put("openDataServer", new OpenServerCommand());
        commands.put("connect", new ConnectCommand());
        commands.put("disconnect", new DisconnectCommand());
        commands.put("var", new DefineVarCommand(symbolTable));
        commands.put("=", new AssignmentCommand(symbolTable));
        commands.put("if", new IfCommand(symbolTable, commands, returnCommand));
        commands.put("while", new LoopCommand(symbolTable, commands, returnCommand));
    }

    public int interpret(String script) {
        return parser.parse(lexer.lex(script), commands, returnCommand);
    }

    public int interpret(String[] lines) {
        return interpret(String.join("\n", lines));
    }

    public Interpreter useLexer(Lexer lexer) {
        this.lexer = lexer;
        return this;
    }

    public Interpreter useParser(Parser parser) {
        this.parser = parser;
        return this;
    }
}
