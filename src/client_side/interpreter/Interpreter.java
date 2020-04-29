package client_side.interpreter;

import client_side.interpreter.commands.*;

import java.util.HashMap;
import java.util.Map;

public class Interpreter {
    private Lexer lexer = new DefaultLexer();
    private Parser parser = new DefaultParser();

    private Map<String, Command> commands = new HashMap<>();
    private Map<String, Double> symbolTable = new HashMap<>();

    public Interpreter() {
        commands.put("return", new ReturnCommand(symbolTable));
        commands.put("openDataServer", new OpenServerCommand());
        commands.put("connect", new ConnectCommand());
        commands.put("disconnect", new DisconnectCommand());
        commands.put("var", new DefineVarCommand(symbolTable));
        commands.put("=", new AssignmentCommand(symbolTable));
        commands.put("if", new IfCommand(symbolTable, commands));
        commands.put("while", new LoopCommand(symbolTable, commands));
        commands.put("block", new BlockCommand(symbolTable, commands));

        symbolTable.put(null,new Double(0));//symbolTable[null] <=> IsReturned
    }

    public Integer interpret(String script) {

        try {
            return commands.get("block").doCommand(lexer.lex(script),0);
        } catch (CannotInterpretException e) {
            System.out.println("unhandled exception:\n" +
                    "token number:" +e.token_index +"\n"+
                    e.error_message);
        }
        return  null;
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
