package client_side.interpreter;

public class CannotInterpretException extends Exception {
    public int token_index;
    public String error_message;
    public CannotInterpretException(String m, int i){
        this.token_index=i;
        this.error_message=m;
    }
}
