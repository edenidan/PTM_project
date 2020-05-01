package client_side.interpreter;

import java.util.Arrays;

public class DefaultLexer implements Lexer {
    private final static String tokenToSpaceRegex = "==|>=|<=|[{}()<>=!+\\-*/]";

    @Override
    public String[] lex(String script) {
        String[] res = insertSpacesBeforeAfter(script).split("\\s+");

        if(res[0].isEmpty())
            return Arrays.copyOfRange(res,1,res.length);
        return res;
    }

    private String insertSpacesBeforeAfter(String script) {
        return script.replaceAll(tokenToSpaceRegex, " $0 ");
    }
}
