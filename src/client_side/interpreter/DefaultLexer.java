package client_side.interpreter;

public class DefaultLexer implements Lexer {
    private final char[] spacingChars = "<>(){}=+-*/".toCharArray();

    @Override
    public String[] lex(String script) {
        return insertSpacesBeforeAfter(script).split("\\s+");
    }

    private String insertSpacesBeforeAfter(String script) {
        for (Character c : spacingChars) {
            script = script.replace(c.toString(), String.format(" %c ", c));
        }
        return script;
    }
}
