package client_side.interpreter.commands;

import client_side.Wrapper;
import client_side.interpreter.BlockEdgeFinder;
import client_side.interpreter.CannotInterpretException;
import client_side.interpreter.Command;
import client_side.interpreter.ConditionParser;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class LoopCommand implements Command {
    private final ConcurrentMap<String, Double> symbolTable;

    private final Map<String, Command> commands;
    private final Wrapper<Boolean> returned;

    public LoopCommand(ConcurrentMap<String, Double> symbolTable, Map<String, Command> commands, Wrapper<Boolean> returned) {
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
            int retVal = commands.get("block").doCommand(tokens, blockStart + 1);
            if (returned.get())
                return retVal;
        }
        return blockEnd + 1;
    }
}
