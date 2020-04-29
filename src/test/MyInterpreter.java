package test;

import client_side.interpreter.Interpreter;

public class MyInterpreter {
    public static int interpret(String[] lines) {
        return new Interpreter().interpret(lines);
    }
}
