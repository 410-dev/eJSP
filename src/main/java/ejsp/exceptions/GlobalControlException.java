package ejsp.exceptions;

public class GlobalControlException extends Exception{
    public GlobalControlException(String exceptionMessage, Throwable err) {
        super(exceptionMessage, err);
    }
    public GlobalControlException(String exceptionMessage) {
        super(exceptionMessage);
    }
}
