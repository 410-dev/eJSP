package ejsp.exceptions;

public class PayloadExpiredException extends Exception{
    public PayloadExpiredException(String exceptionMessage) {
        super(exceptionMessage);
    }
}
