package client_side.interpreter.commands;

import utility.Wrapper;
import client_side.interpreter.CannotInterpretException;
import client_side.interpreter.Command;

import java.util.List;
import java.util.Map;

public class BlockCommand implements Command {
    private final Map<String, Command> commands;
    private final Wrapper<Integer> returned;

    public BlockCommand(Map<String, Command> commands, Wrapper<Integer> returned) {
        this.commands = commands;
        this.returned = returned;
    }

    @Override
    public int doCommand(List<String> tokens, int startIndex) throws CannotInterpretException {
        int i = startIndex;
        while (i < tokens.size()) {
            String currentToken = tokens.get(i);

            if (currentToken.equals("}")) // end of block
                return i + 1;

            Command command = commands.get(currentToken);

            if (command != null) { // able to get command
                int returnedFromCommand = command.doCommand(tokens, i); // do command and advance current token by return value of command

                if (returned.get() != null) // returned
                    return returnedFromCommand; // supposed to be ignored
                else
                    i = returnedFromCommand;
            } else if (tokens.get(i + 1).equals("=")) { // variable name
                i++;
            } else {
                throw new CannotInterpretException("Cannot find symbol: " + currentToken, i);
            }
        }
        return i;
    }
}
