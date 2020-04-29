package client_side.interpreter;

import java.util.Map;

public class DefaultParser implements Parser {
    @Override
    public int parse(String[] tokens, Map<String, Command> commands, ReturnCommand returnCommand) {
        for (int i = 0; i < tokens.length; ) {
            String currentToken = tokens[i];
            Command command = commands.get(currentToken);
            if (command != null) // able to get command
                i += command.doCommand(tokens, i); // do command and advance current token by return value of command
            else if (currentToken.equals("return"))
                return returnCommand.doReturn(tokens, i); // evaluate the expression after the return and return it
            else if (tokens[i + 1].equals("=")) // variable name
                i++;
            else
                throw new IllegalArgumentException("Parse error at token index " + i);
        }
        return 0;
    }
}
