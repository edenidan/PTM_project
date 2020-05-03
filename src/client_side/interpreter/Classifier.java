package client_side.interpreter;

import client_side.interpreter.math.BinaryOperator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class Classifier {

    final private static HashSet<String> binaryOperators = new HashSet<String>();

    public static boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isVariable(String token, ConcurrentMap<String, Variable> symbolTable) {
        return symbolTable.containsKey(token);
    }

    public static boolean isOperator(String token) {
        return binaryOperators.contains(token);
    }

    static {
        binaryOperators.add("+");
        binaryOperators.add("-");
        binaryOperators.add("*");
        binaryOperators.add("/");
    }

}
