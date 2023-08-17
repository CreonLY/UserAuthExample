package entities.exceptions;

public class AuthorizationFailException extends BaseException {
    public AuthorizationFailException() {
        super();
    }

    public AuthorizationFailException(String message) {
        super(message);
    }

    public AuthorizationFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorizationFailException(Throwable cause) {
        super(cause);
    }
}
