package client_side.interpreter;

import java.util.List;

public interface ConditionParser {
    //index of the first operand
    Boolean parse(List<String> tokens, int startIndex) throws CannotInterpretException;
}
