package math;

public interface ValueParser<T> {
    boolean isValue(String token);

    T toValue(String token) throws IllegalArgumentException;
}
