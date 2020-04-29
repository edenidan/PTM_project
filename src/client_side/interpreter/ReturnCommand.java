package client_side.interpreter;

import java.util.Map;

public class ReturnCommand {
    private Map<String, Double> symbolTable;

    public ReturnCommand(Map<String, Double> symbolTable) {
        this.symbolTable = symbolTable;
    }

    public int doReturn(String[] tokens, int startIndex) {
        // close any open servers / clients
        return 0;
    }
}
