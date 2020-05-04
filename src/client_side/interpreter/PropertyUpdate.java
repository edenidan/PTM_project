package client_side.interpreter;

public class PropertyUpdate {
    private final String property;
    private final double value;

    public PropertyUpdate(String property, double value) {
        this.property = property;
        this.value = value;
    }

    public String getProperty() {
        return property;
    }

    public double getValue() {
        return value;
    }
}
