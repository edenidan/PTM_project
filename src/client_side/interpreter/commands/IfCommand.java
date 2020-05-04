package client_side.interpreter.commands;

import client_side.interpreter.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class IfCommand implements Command {
    private final ConcurrentMap<String, Variable> symbolTable;
    private final Map<String, Command> commands;

    public IfCommand(ConcurrentMap<String, Variable> symbolTable, Map<String, Command> commands) {
        this.symbolTable = symbolTable;
        this.commands = commands;
    }

    @Override
    public int doCommand(List<String> tokens, int startIndex) throws CannotInterpretException {
        int blockStart = BlockEdgeFinder.getBlockStart(tokens, startIndex);
        int blockEnd = BlockEdgeFinder.getBlockEnd(tokens, startIndex);
        if (blockEnd == -1 || blockStart == -1)
            throw new CannotInterpretException("Wrong {, } positions", startIndex);

        if (ConditionParser.parse(tokens, startIndex + 1, symbolTable)) {
            return commands.get("block").doCommand(tokens, blockStart + 1);
        } else {
            return blockEnd + 1;
        }
    }
}
