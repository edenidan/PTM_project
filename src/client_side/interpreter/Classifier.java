package client_side.interpreter;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class Classifier {
    final private static Set<String> binaryOperators = new HashSet<>();

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

    public static boolean isPort(Double port) {
        return port.intValue() == port && 0 < port && port < Math.pow(2, 16);
    }

    public static boolean isAddress(String ip) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

        return ip.matches(PATTERN);
    }

    public static boolean isInt(double value) {
        return (value % 1) == 0;
    }

    static {
        binaryOperators.add("+");
        binaryOperators.add("-");
        binaryOperators.add("*");
        binaryOperators.add("/");
    }
}
