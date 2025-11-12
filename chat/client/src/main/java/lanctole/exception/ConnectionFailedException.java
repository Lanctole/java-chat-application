package lanctole.exception;

public class ConnectionFailedException extends RuntimeException {
    public ConnectionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
    public ConnectionFailedException(String message) {
        super(message);
    }
}
