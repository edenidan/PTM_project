package client_side.interpreter;

import java.util.Map;

public interface Parser {
    int parse(String[] tokens, Map<String, Command> commands, ReturnCommand returnCommand);
}
