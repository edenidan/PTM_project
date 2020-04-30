package client_side.interpreter;

public interface ConditionParser {
    //index of the first operand
    public Boolean parse(String[] tokens,int start_index) throws CannotInterpretException;
}
