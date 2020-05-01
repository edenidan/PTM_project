package client_side.interpreter;

import java.util.List;

public interface Lexer {
    List<String> lex(String script);
}
