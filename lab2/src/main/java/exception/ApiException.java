package exception;

public class ApiException extends Exception {

    public ApiException(String message) {
        super(message);
        System.err.println("ApiException: " + message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
        System.err.println("ApiException: " + message);
        System.err.println("Caused by: " + cause.getMessage());
    }
}