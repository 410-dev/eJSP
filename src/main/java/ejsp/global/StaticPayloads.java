package ejsp.global;

import ejsp.Payload;
import ejsp.exceptions.GlobalControlException;
import java.util.ArrayList;

public class StaticPayloads {
    private static ArrayList<Payload> globalPayloadList = new ArrayList<>();
    private static ArrayList<String> globalPayloadListName = new ArrayList<>();

    private static boolean parallelArrayMatch() {
        return globalPayloadListName.size() == globalPayloadList.size();
    }

    public static void addPayload(Payload initializedPayload, String payloadName) throws GlobalControlException {
        if (globalPayloadListName.contains(payloadName)) {
            throw new GlobalControlException("The payload with the name of \"" + payloadName + "\" already exists.");
        }else{
            globalPayloadList.add(initializedPayload);
            globalPayloadListName.add(payloadName);
        }
    }

    public static ArrayList<Payload> getAllPayloads() throws GlobalControlException {
        if (!parallelArrayMatch()){
            throw new GlobalControlException("The payload list and the name list is broken. (Internal Exception)");
        }else{
            return globalPayloadList;
        }
    }

    public static Payload getPayload(String payloadName) throws GlobalControlException {
        if (!globalPayloadListName.contains(payloadName)) {
            throw new GlobalControlException("The payload with the name of \"" + payloadName + "\" does not exist.");
        }else if (!parallelArrayMatch()){
            throw new GlobalControlException("The payload list and the name list is broken. (Internal Exception)");
        }else{
            return globalPayloadList.get(globalPayloadListName.indexOf(payloadName));
        }
    }

    public static void removePayload(int indexOf) throws GlobalControlException {
        if (globalPayloadList.size() <= indexOf) {
            throw new GlobalControlException("Payload index out of bound.");
        }else if (!parallelArrayMatch()) {
            throw new GlobalControlException("The payload list and the name list is broken. (Internal Exception)");
        }else {
            globalPayloadList.remove(indexOf);
            globalPayloadListName.remove(indexOf);
        }
    }

    public static void removePayload(String payloadName) throws GlobalControlException {
        if (!globalPayloadListName.contains(payloadName)) {
            throw new GlobalControlException("Payload not found.");
        }else if (!parallelArrayMatch()) {
            throw new GlobalControlException("The payload list and the name list is broken. (Internal Exception)");
        }else{
            globalPayloadList.remove(globalPayloadListName.indexOf(payloadName));
            globalPayloadListName.remove(globalPayloadListName.indexOf(payloadName));
        }
    }

    public static int getPayloadsCounts() {
        return globalPayloadList.size();
    }
}
