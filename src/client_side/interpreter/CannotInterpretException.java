package client_side.interpreter;

public class CannotInterpretException extends Exception {
    public int tokenIndex;
    public String errorMessage;

    public CannotInterpretException(String errorMessage, int tokenIndex) {
        this.tokenIndex = tokenIndex;
        this.errorMessage = errorMessage;
    }
}
