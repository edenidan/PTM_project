package client_side.interpreter;

import test.MainTrain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class DefaultLexer implements Lexer {
    @Override
    public String[] lex(String script) {
        script = BetweenSpaces(script,"<>(){}=+-*/");
        Scanner s = new Scanner(script);
        ArrayList<String> tokens = new ArrayList<>();

        while(s.hasNext())
            tokens.add(s.next());

        String[] result = new String[tokens.size()];
        return tokens.toArray(result);
    }

    private String BetweenSpaces(String script,String chars){
        String space = " ";
        for (Character c:chars.toCharArray()) {
            String from = c.toString();
            String to = space+c+space;
            script=script.replace(from,to);
        }
        return script;
    }
}
