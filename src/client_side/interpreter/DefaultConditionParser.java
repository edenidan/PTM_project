package client_side.interpreter;

import javax.xml.ws.Provider;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class DefaultConditionParser implements ConditionParser {

    private Map<String, Double> symbolTable;
    public DefaultConditionParser(Map<String, Double> symbolTable){
        this.symbolTable = symbolTable;
    }
    private double getValue(String operand){
        Double value=symbolTable.get(operand);
        if(value == null){
            value = Double.parseDouble(operand);
        }
        return value;
    }

    private Boolean isOperator(String token){
        if(token==null)
            return false;
        return token.equals("=")
                || token.equals("<")
                || token.equals(">")
                || token.equals("!");
    }
    private int getOperatorSize(String[] tokens, int start_index){
        int operator_size=0;
        operator_size += isOperator(tokens[start_index+1])?1:0;
        operator_size += isOperator(tokens[start_index+2])?1:0;

        return operator_size;
    }

    @Override
    public Boolean parse(String[] tokens, int start_index) throws CannotInterpretException {
        //the operator may be one token or two
        int operator_size = getOperatorSize(tokens,start_index);

        double operand1,operand2;
        try {
            operand1 = getValue(tokens[start_index]);
            operand2 = getValue(tokens[start_index + operator_size + 1]);
        }
        catch (NumberFormatException e) {
            throw new CannotInterpretException("Cannot resolve condition operands",start_index);
        }

        switch (tokens[start_index+1] + (operator_size==2?tokens[start_index+2]:"" )){
            case "==":
                return operand1 == operand2;
            case ">=":
                return operand1 >= operand2;
            case "<=":
                return operand1 <= operand2;
            case ">":
                return operand1 > operand2;
            case "<":
                return operand1 < operand2;
            case "!=":
                return operand1 != operand2;
        }
        throw new CannotInterpretException("Unknown operator",start_index);

    }
}
