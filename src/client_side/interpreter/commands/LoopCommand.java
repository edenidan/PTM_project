package client_side.interpreter.commands;

import client_side.Wrapper;
import client_side.interpreter.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class LoopCommand implements Command {
    private final ConcurrentMap<String, Variable> symbolTable;

    private final Map<String, Command> commands;
    private final Wrapper<Integer> returned;

    public LoopCommand(ConcurrentMap<String, Variable> symbolTable, Map<String, Command> commands, Wrapper<Integer> returned) {
        this.symbolTable = symbolTable;
        this.commands = commands;
        this.returned = returned;
    }

    @Override
    public int doCommand(List<String> tokens, int startIndex) throws CannotInterpretException {
        int blockStart = BlockEdgeFinder.getBlockStart(tokens, startIndex);
        int blockEnd = BlockEdgeFinder.getBlockEnd(tokens, startIndex);

        if (blockStart == -1 || blockEnd == -1)
            throw new CannotInterpretException("Wrong {, } positions", startIndex);

        while (ConditionParser.parse(tokens, startIndex + 1, symbolTable)) {
            commands.get("block").doCommand(tokens, blockStart + 1);
            if (returned.get() != null)
                return blockEnd + 1; // can be anything, calling command is supposed to ignore anyway
        }
        return blockEnd + 1;
    }
}
