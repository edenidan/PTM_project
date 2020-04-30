package client_side.interpreter;

public interface ConditionParser {
    //index of the first operand
    Boolean parse(String[] tokens, int startIndex) throws CannotInterpretException;
}
