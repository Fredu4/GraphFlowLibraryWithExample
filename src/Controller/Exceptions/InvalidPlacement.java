package Controller.Exceptions;

public class InvalidPlacement extends RuntimeException {
    public InvalidPlacement(String message){
        super(message);
    }
}
