package ejsp.data;

import java.io.PrintWriter;
import java.io.StringWriter;

public class PageExceptionData {
    private Exception exception = null;
    private String exceptionData = null;
    private String exceptionLocation = "location_undefined";

    public PageExceptionData(Exception e) {
        exception = e;
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        exceptionData = sw.toString();
    }

    public PageExceptionData(Exception e, String exceptionLocation) {
        this.exceptionLocation = exceptionLocation;
        exception = e;
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        exceptionData = sw.toString();
    }

    public Exception getException() {
        return exception;
    }

    public String getExceptionStringData() {
        return exceptionData;
    }

    public String getExceptionLocation() {
        return exceptionLocation;
    }

    public String toString() {
        return "Error Location: " + exceptionLocation + " / Stack Trace: " + exceptionData;
    }

}
