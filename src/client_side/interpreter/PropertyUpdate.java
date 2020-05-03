package client_side.interpreter;

public class PropertyUpdate {
    String property;
    double value;
    public PropertyUpdate(String property,double value){
        this.property=property;
        this.value = value;
    }


    public String getProperty(){return property;}
    public void setProperty(String property){this.property=property;}
    public double getValue(){return value;}
    public void setValue(double value){this.value=value;}
}
