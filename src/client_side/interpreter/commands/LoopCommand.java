package client_side.interpreter.commands;

import client_side.Wrapper;
import client_side.interpreter.*;
import client_side.interpreter.math.LogicParser;

import java.util.List;
import java.util.Map;

public class LoopCommand implements Command {
    private final Map<String, Double> symbolTable;

    private final Map<String, Command> commands;
    private final Wrapper<Boolean> returned;

    public LoopCommand(Map<String, Double> symbolTable, Map<String, Command> commands, Wrapper<Boolean> returned) {
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

        while (LogicParser.parse(tokens, blockStart, symbolTable)) {
            int retVal = commands.get("block").doCommand(tokens, blockStart + 1);
            if (returned.get())
                return retVal;
        }
        return blockEnd;
    }
}
