package client_side.interpreter;

public class DefaultLexer implements Lexer {
//    private final static char[] spacingChars = "!<>(){}=+-*/".toCharArray();
    private final static String tokenToSpaceRegex = "==|>=|<=|[{}()<>=!+\\-*/]";

    @Override
    public String[] lex(String script) {
        return insertSpacesBeforeAfter(script).split("\\s+");
    }

    private String insertSpacesBeforeAfter(String script) {
        return script.replaceAll(tokenToSpaceRegex, " $0 ");
//        for (Character c : spacingChars) {
//            script = script.replace(c.toString(), String.format(" %c ", c));
//        }
//        return script;
    }
}
