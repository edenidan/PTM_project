package client_side.interpreter.commands;

import client_side.Wrapper;
import client_side.interpreter.CannotInterpretException;
import client_side.interpreter.Command;
import client_side.interpreter.DefaultConditionParser;

import java.util.Map;
import java.util.Stack;

public class IfCommand implements Command {
    private Map<String, Double> symbolTable;
    private Map<String, Command> commands;

    public IfCommand(Map<String, Double> symbolTable, Map<String, Command> commands) {
        this.symbolTable = symbolTable;
        this.commands = commands;
    }

    @Override
    public int doCommand(String[] tokens, int startIndex) throws CannotInterpretException {

        int blockStart = getBlockStart(tokens,startIndex);
        int blockEnd = getBlockEnd(tokens,startIndex);
        if(blockEnd == -1 || blockStart == -1)
            throw new CannotInterpretException("Wrong {, } positions",startIndex);

        int retVal=blockEnd;
        if(new DefaultConditionParser(symbolTable).parse(tokens,startIndex+1)){
            retVal = commands.get("block").doCommand(tokens,blockStart+1);
        }

        return retVal;
    }

    private int getBlockStart(String[] tokens, int startIndex) {
        for(;startIndex<tokens.length;startIndex++){
            if("{".equals(tokens[startIndex]))
                return startIndex;
        }
        return -1;
    }

    private int getBlockEnd(String[] tokens, int startIndex) {
        int numberOfOpens=0;
        startIndex=getBlockStart(tokens,startIndex);

        for(;startIndex<tokens.length;startIndex++){
            if(tokens[startIndex].equals("}"))
                numberOfOpens--;

            if(tokens[startIndex].equals("{"))
                numberOfOpens++;

            if(numberOfOpens==0)
                return startIndex;
        }
        return -1;
    }
}
