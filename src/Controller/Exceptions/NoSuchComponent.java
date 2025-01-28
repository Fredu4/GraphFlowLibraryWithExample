package Controller.Exceptions;

public class NoSuchComponent extends RuntimeException {
    public NoSuchComponent(String message){
        super(message);
    }
}
