package ejsp;

import ejsp.exceptions.PayloadExpiredException;
import ejsp.exceptions.PayloadValueException;

import java.util.ArrayList;

public class Payload {
    private ArrayList<String> id = new ArrayList<>();
    private ArrayList<String> value = new ArrayList<>();

    private int referenceCountLimit = -1;
    private int referenceCounts = 0;

    public Payload() {}
    public Payload(int referenceCountLimit) {
        setReferenceCountLimit(referenceCountLimit);
    }

    private void checkParallelArrayMatch() throws PayloadValueException{
        if (id.size() != value.size()){
            throw new PayloadValueException("The value list and the name list is broken. (Internal Exception)");
        }
    }

    public void setReferenceCountLimit(int referenceCountLimit) {
        this.referenceCountLimit = referenceCountLimit;
    }

    public void addValue(String id, String value) throws PayloadValueException {
        if (id == null) {
            throw new PayloadValueException("Given ID is null.");
        }else if (!this.id.contains(id)) {
            this.id.add(id);
            this.value.add(value);
        }else{
            throw new PayloadValueException("There is already a value with id: " + id);
        }
    }

    public void removeValue(String id) throws PayloadValueException {
        if (id == null) {
            throw new PayloadValueException("Given ID is null.");
        }else if (this.id.contains(id)) {
            value.remove(this.id.indexOf(id));
            this.id.remove(id);
        }else{
            throw new PayloadValueException("There is no value with id: " + id);
        }
    }

    public void removeValue(int index) throws PayloadValueException {
        if (id.size() <= index) {
            throw new PayloadValueException("Payload index out of bound.");
        }else {
            value.remove(index);
            id.remove(index);
        }
    }

    public String[][] getAllValues() throws PayloadExpiredException, PayloadValueException {
        if (id.size() == 0) {
            throw new PayloadValueException("Payload is empty.");
        }

        checkParallelArrayMatch();

        if (referenceCountLimit != -1) {
            referenceCounts ++;
        }
        if (referenceCounts == referenceCountLimit) {
            id.clear();
            value.clear();
            throw new PayloadExpiredException("Payload expired: Reading was limited to " + referenceCountLimit + " times.");
        }else{
            String[][] toReturn = new String[2][id.size()];
            for(int i = 0; i < id.size(); i++) {
                toReturn[0][i] = id.get(i);
                toReturn[1][i] = value.get(i);
            }
            return toReturn;
        }
    }

    public String getValueAt(int index) throws PayloadValueException, PayloadExpiredException {
        if (referenceCountLimit != -1) {
            referenceCounts ++;
        }

        checkParallelArrayMatch();

        if (referenceCounts == referenceCountLimit) {
            id.clear();
            value.clear();
            throw new PayloadExpiredException("Payload expired: Reading was limited to " + referenceCountLimit + " times.");
        }else {
            if (id.size() <= index) {
                throw new PayloadValueException("Payload index out of bound.");
            } else {
                return value.get(index);
            }
        }
    }

    public String getValueOf(String id) throws PayloadValueException, PayloadExpiredException {
        if (referenceCountLimit != -1) {
            referenceCounts ++;
        }

        checkParallelArrayMatch();

        if (referenceCounts == referenceCountLimit) {
            this.id.clear();
            value.clear();
            throw new PayloadExpiredException("Payload expired: Reading was limited to " + referenceCountLimit + " times.");
        }else {
            if (!this.id.contains(id)) {
                throw new PayloadValueException("Payload does not contain such value: " + id);
            } else {
                return value.get(id.indexOf(id));
            }
        }
    }

    public int getPayloadSize() {
        return id.size();
    }
}
