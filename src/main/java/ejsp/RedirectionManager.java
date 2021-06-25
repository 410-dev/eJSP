package ejsp;

import ejsp.data.Payload;
import ejsp.exceptions.PayloadExpiredException;
import ejsp.exceptions.PayloadValueException;
import ejsp.exceptions.RedirectionManagerException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

// Similar with POST method (perhaps multiple) redirection(s), but using HttpSession

public class RedirectionManager {

    private Payload payload = new Payload();

    private String shouldRedirectTo;

    HttpSession session;
    HttpServletResponse response;

    public RedirectionManager(HttpSession session, HttpServletResponse response) {
        this.session = session;
        this.response = response;
    }

    public void addParameter(String id, String value) throws RedirectionManagerException{
        try{
            payload.addValue(id, value);
        }catch(Exception e) {
            throw new RedirectionManagerException(e.toString(), e);
        }
    }

    public void setRedirectionDestination(String url) {
        if (url.endsWith("?")) {
            shouldRedirectTo = url.substring(0, url.length() - 1);
        }else{
            shouldRedirectTo = url;
        }
    }

    // IF there is a necessary multiple redirections, then this method will redirect to the page before final destination
    public void redirectBeforeProcess(String url) throws IOException, RedirectionManagerException {
        if (shouldRedirectTo == null) {
            throw new RedirectionManagerException("Redirection destination after required process completion is not defined.");
        }else {
            session.setAttribute("RedirectionPayload", payload);
            response.sendRedirect(url);
        }
    }

    public void redirectAfterProcess() throws IOException, PayloadValueException, PayloadExpiredException {
        String redirectionAddress = shouldRedirectTo + "?";
        String[][] payloadData = payload.getAllValues();
        for(int i = 0; i < payload.getPayloadSize(); i++) {
            redirectionAddress += payloadData[0][i] + "=";
            redirectionAddress += payloadData[1][i];
            if (i + 1 < payload.getPayloadSize()) {
                redirectionAddress += "&";
            }
        }
        session.removeAttribute("RedirectionPayload");
        response.sendRedirect(redirectionAddress);
    }

    public Payload getPayload() throws RedirectionManagerException{
        if (session.getAttribute("RedirectionPayload") == null) {
            throw new RedirectionManagerException("Payload is not properly generated from previous page.");
        }
        return (Payload) session.getAttribute("RedirectionPayload");
    }
}
