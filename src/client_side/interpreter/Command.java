package client_side.interpreter;

import java.util.List;

public interface Command {
    int doCommand(List<String> tokens, int startIndex) throws CannotInterpretException;
}
