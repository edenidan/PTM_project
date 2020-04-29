package client_side.interpreter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class DefaultLexer implements Lexer {
    @Override
    public String[] lex(String script) {
        Scanner s = new Scanner(script);
        ArrayList<String> tokens = new ArrayList<String>();

        while(s.hasNext())
            tokens.add(s.next());

        String[] result = new String[tokens.size()];
        return tokens.toArray(result);
    }
}
