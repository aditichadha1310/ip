package exception;

public class NoTaskFoundException extends Exception {
    public NoTaskFoundException(String errorMessage) {
        super(errorMessage);
    }
}
