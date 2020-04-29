package client_side.interpreter;

public class DefaultLexer implements Lexer {
    @Override
    public String[] lex(String script) {
        return script.split("\\s+");
    }
}
