package client_side;

public class Wrapper<T> {
    private T t;
    public Wrapper(T t){
        this.t=t;
    }

    public  T Get(){
        return this.t;
    }

    public void Set(T t){
        this.t=t;
    }
}
