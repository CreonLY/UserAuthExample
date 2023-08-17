package entities.exceptions;

public class DataDuplicateException extends BaseException {
    public DataDuplicateException() {
        super();
    }

    public DataDuplicateException(String message) {
        super(message);
    }

    public DataDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataDuplicateException(Throwable cause) {
        super(cause);
    }

}
