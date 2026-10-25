package exception;

public class EmptyEntryException extends Exception {

    // Creates an exception with a message about the empty entry.
    public EmptyEntryException(String message) {
        super(message);
    }
}
