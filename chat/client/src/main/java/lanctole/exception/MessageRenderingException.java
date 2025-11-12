package lanctole.exception;

public class MessageRenderingException extends RuntimeException {
    public MessageRenderingException(String message, Throwable cause) {
        super(message, cause);
    }
}
