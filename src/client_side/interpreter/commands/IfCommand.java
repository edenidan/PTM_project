package client_side.interpreter.commands;

import client_side.interpreter.BlockEdgeFinder;
import client_side.interpreter.CannotInterpretException;
import client_side.interpreter.Command;
import client_side.interpreter.DefaultConditionParser;

import java.util.List;
import java.util.Map;

public class IfCommand implements Command {
    private final Map<String, Double> symbolTable;
    private final Map<String, Command> commands;

    public IfCommand(Map<String, Double> symbolTable, Map<String, Command> commands) {
        this.symbolTable = symbolTable;
        this.commands = commands;
    }

    @Override
    public int doCommand(List<String> tokens, int startIndex) throws CannotInterpretException {

        int blockStart = BlockEdgeFinder.getBlockStart(tokens, startIndex);
        int blockEnd = BlockEdgeFinder.getBlockEnd(tokens, startIndex);
        if (blockEnd == -1 || blockStart == -1)
            throw new CannotInterpretException("Wrong {, } positions", startIndex);

        int retVal = blockEnd;
        if (new DefaultConditionParser(symbolTable).parse(tokens, startIndex + 1)) {
            retVal = commands.get("block").doCommand(tokens, blockStart + 1);
        }

        return retVal;
    }
}
