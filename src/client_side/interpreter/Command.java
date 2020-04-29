package client_side.interpreter;

public interface Command {
    int doCommand(String[] tokens, int startIndex);
}
