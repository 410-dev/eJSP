package ejsp.exceptions;

public class RedirectionManagerException extends Exception{
    public RedirectionManagerException(String exceptionMessage) {
        super(exceptionMessage);
    }
    public RedirectionManagerException(String exceptionMessage, Throwable err) {
        super(exceptionMessage, err);
    }
}
