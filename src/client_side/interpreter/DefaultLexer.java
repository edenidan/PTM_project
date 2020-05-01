package client_side.interpreter;

public class DefaultLexer implements Lexer {
    private final static String tokenToSpaceRegex = "==|>=|<=|[{}()<>=!+\\-*/]";

    @Override
    public String[] lex(String script) {
        return insertSpacesBeforeAfter(script).split("\\s+");
    }

    private String insertSpacesBeforeAfter(String script) {
        return script.replaceAll(tokenToSpaceRegex, " $0 ");
    }
}
