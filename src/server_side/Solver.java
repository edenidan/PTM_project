package server_side;

public interface Solver<T, U> {
    U solve(T problem);
}
