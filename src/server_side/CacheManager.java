package server_side;

public interface CacheManager<T, U> {
    boolean existsSolution(T problem);
    U getSolution(T problem);
    void saveSolution(T problem, U solution);
}
