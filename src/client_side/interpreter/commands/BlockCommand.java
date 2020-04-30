package client_side.interpreter.commands;

import client_side.interpreter.CannotInterpretException;
import client_side.interpreter.Command;
import client_side.*;

import java.util.Map;

public class BlockCommand implements Command {

    private Map<String, Double> symbolTable;
    private Map<String, Command> commands;
    private Wrapper<Boolean> returned;

    public BlockCommand(Map<String, Double> symbolTable, Map<String, Command> commands, Wrapper<Boolean> returned) {
        this.symbolTable = symbolTable;
        this.commands = commands;
        this.returned=returned;
    }

    @Override
    public int doCommand(String[] tokens, int startIndex) throws CannotInterpretException {
        int i=startIndex;
        for (; i < tokens.length; ) {
            String currentToken = tokens[i];
            Command command = commands.get(currentToken);

            if(currentToken.equals("}"))//end of block
                return i+1;

            int returned_from_command=0;

            if (command != null) // able to get command
                returned_from_command = command.doCommand(tokens, i); // do command and advance current token by return value of command
            else if (tokens[i + 1].equals("=")) // variable name
                i++;
            else
                throw new CannotInterpretException("Cannot find symbol: "+currentToken,i);

            if(returned.get())//returned
                return returned_from_command;
            else
                i+=returned_from_command;
        }
        return i;
    }
}
