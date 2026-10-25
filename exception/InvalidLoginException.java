package exception;

public class InvalidLoginException extends Exception {

    // Creates an exception with a message about the failed login.
    public InvalidLoginException(String message) {
        super(message);
    }
}
