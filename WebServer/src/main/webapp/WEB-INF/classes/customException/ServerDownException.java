package classes.customException;

public class ServerDownException extends Exception {
    public ServerDownException(String message) {
        super(message);
    }
}
