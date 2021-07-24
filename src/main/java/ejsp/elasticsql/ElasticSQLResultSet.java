package ejsp.elasticsql;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.sql.ResultSet;
import java.util.ArrayList;

public class ElasticSQLResultSet {
    private ArrayList<String> id = new ArrayList<>();
    private ArrayList<String> data = new ArrayList<>();
    private ArrayList<String> index = new ArrayList<>();

    private int currentlySelectedIndex = -1;
    private final int maximumIndex;

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATA = "data";
    public static final String COLUMN_INDEX = "indexes";

    public ElasticSQLResultSet(ResultSet rs) throws Exception {
        while(rs.next()) {
            id.add(rs.getString(COLUMN_ID));
            data.add(rs.getString(COLUMN_DATA));
            index.add(rs.getString(COLUMN_INDEX));
        }
        maximumIndex = id.size() - 1;
    }

    public ElasticSQLResultSet(ArrayList<String> id, ArrayList<String> data, ArrayList<String> index) throws Exception {
        this.id = id;
        this.data = data;
        this.index = index;
        maximumIndex = id.size() - 1;
    }

    public String getStringData(String key) throws ElasticSQLException {
        if (currentlySelectedIndex < 0) {
            throw new ElasticSQLException("Selected row is before the first row.");
        }else if (currentlySelectedIndex > maximumIndex) {
            throw new ElasticSQLException("Selected row is after the last row.");
        }
        try{
            JSONParser parser = new JSONParser();
            String innerJSON = data.get(currentlySelectedIndex);
            JSONObject jsonData = (JSONObject) parser.parse(innerJSON);
            return jsonData.get(key).toString();
        }catch (Exception e) {
            throw new ElasticSQLException("Key \"" + key + "\" not found.");
        }
    }

    public String getEntireData() throws ElasticSQLException {
        if (currentlySelectedIndex < 0) {
            throw new ElasticSQLException("Selected row is before the first row.");
        }else if (currentlySelectedIndex > maximumIndex) {
            throw new ElasticSQLException("Selected row is after the last row.");
        }
        return data.get(currentlySelectedIndex);
    }

    public String getIndex() throws ElasticSQLException {
        if (currentlySelectedIndex < 0) {
            throw new ElasticSQLException("Selected row is before the first row.");
        }else if (currentlySelectedIndex > maximumIndex) {
            throw new ElasticSQLException("Selected row is after the last row.");
        }
        return index.get(currentlySelectedIndex);
    }

    public String getID() throws ElasticSQLException {
        if (currentlySelectedIndex < 0) {
            throw new ElasticSQLException("Selected row is before the first row.");
        }else if (currentlySelectedIndex > maximumIndex) {
            throw new ElasticSQLException("Selected row is after the last row.");
        }
        return id.get(currentlySelectedIndex);
    }

    public void first() throws ElasticSQLException {
        if (maximumIndex < 0) throw new ElasticSQLException("Data length is 0.");
        currentlySelectedIndex = 0;
    }

    public void last() throws ElasticSQLException {
        if (maximumIndex < 0) throw new ElasticSQLException("Data length is 0.");
        currentlySelectedIndex = maximumIndex;
    }

    public boolean previous() {
        if (currentlySelectedIndex < 1) return false;
        currentlySelectedIndex--;
        return true;
    }

    public boolean next() {
        if (currentlySelectedIndex > maximumIndex - 1) return false;
        currentlySelectedIndex++;
        return true;
    }

    public boolean select(int index) {
        if (maximumIndex < index || index < 0) return false;
        currentlySelectedIndex = index;
        return true;
    }

    public String toString() {
        if (currentlySelectedIndex < 0 || currentlySelectedIndex > maximumIndex) {
            return null;
        }
        return id.get(currentlySelectedIndex) + "\n" + data.get(currentlySelectedIndex) + "\n" + index.get(currentlySelectedIndex);
    }

    public boolean absolute(int index) {
        return select(index);
    }

    public boolean hasKey(String index) {
        return data.get(currentlySelectedIndex).contains(index + ",");
    }

    public int getCurrentlySelectedIndex() {
        return currentlySelectedIndex;
    }

    public int length() {
        return id.size();
    }
}
