package client_side.interpreter.commands;

import client_side.Wrapper;
import client_side.interpreter.CannotInterpretException;
import client_side.interpreter.Command;

import java.util.List;
import java.util.Map;

public class BlockCommand implements Command {
    private final Map<String, Command> commands;
    private final Wrapper<Boolean> returned;

    public BlockCommand(Map<String, Command> commands, Wrapper<Boolean> returned) {
        this.commands = commands;
        this.returned = returned;
    }

    @Override
    public int doCommand(List<String> tokens, int startIndex) throws CannotInterpretException {
        int i = startIndex;
        while (i < tokens.size()) {
            String currentToken = tokens.get(i);
            Command command = commands.get(currentToken);

            if (currentToken.equals("}"))//end of block
                return i + 1;

            int returnedFromCommand = 0;
            if (command != null) // able to get command
                returnedFromCommand = command.doCommand(tokens, i); // do command and advance current token by return value of command
            else if (tokens.get(i + 1).equals("=")) // variable name
                i++;
            else
                throw new CannotInterpretException("Cannot find symbol: " + currentToken, i);

            if (returned.get())//returned
                return returnedFromCommand;
            else
                i += returnedFromCommand;
        }
        return i;
    }
}
