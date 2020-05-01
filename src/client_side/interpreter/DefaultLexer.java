package client_side.interpreter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultLexer implements Lexer {
    private final static String tokenToSpaceRegex = "==|!=|>=|<=|[{}()<>=+\\-*/]";

    @Override
    public List<String> lex(String script) {
        List<String> res = Arrays.asList(insertSpacesBeforeAfter(script).split("\\s+"));

        return res.stream()
                .filter(s->!s.isEmpty())
                .collect(Collectors.toList());
    }

    private String insertSpacesBeforeAfter(String script) {
        return script.replaceAll(tokenToSpaceRegex, " $0 ");
    }
}
