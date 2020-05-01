package client_side.interpreter;

import java.util.Arrays;
import java.util.List;

public class DefaultLexer implements Lexer {
    private final static String tokenToSpaceRegex = "==|!=|>=|<=|[{}()<>=+\\-*/]";

    @Override
    public List<String> lex(String script) {
        List<String> res = Arrays.asList(insertSpacesBeforeAfter(script).split("\\s+"));

        if (res.get(0).isEmpty())
            return res.subList(1, res.size());
        return res;
    }

    private String insertSpacesBeforeAfter(String script) {
        return script.replaceAll(tokenToSpaceRegex, " $0 ");
    }
}
