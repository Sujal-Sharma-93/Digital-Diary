package exception;

public class DuplicateUserException extends Exception {

    // Creates an exception with a message about the duplicate username.
    public DuplicateUserException(String message) {
        super(message);
    }
}
