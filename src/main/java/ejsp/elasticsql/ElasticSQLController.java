package ejsp.elasticsql;

import ejsp.SQLController;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.SQLException;
import java.util.ArrayList;

public class ElasticSQLController {
    private SQLController controller;

    public ElasticSQLController(String protocol, String address, String port, String databaseName, String tableName, String username, String password, String driverName) {
        controller = new SQLController(protocol, address, port, databaseName, tableName, username, password, driverName);
    }

    public ElasticSQLController(String databaseName, String tableName, String username, String password) {
        controller = new SQLController(databaseName, tableName, username, password);
    }

    public ElasticSQLResultSet select() throws Exception {
        return new ElasticSQLResultSet(controller.getAllRows());
    }

    public ElasticSQLResultSet select(int limit) throws Exception {
        return new ElasticSQLResultSet(controller.getAllRows(limit));
    }

    public ElasticSQLResultSet select(String key, String data) throws Exception {
        ElasticSQLResultSet nrs = selectWhereKey(key);
        return equalsSelectionProcess(key, data, nrs);
    }

    public ElasticSQLResultSet select(String key, String data, int limit) throws Exception {
        ElasticSQLResultSet nrs = selectWhereKey(key, limit);
        return equalsSelectionProcess(key, data, nrs);
    }

    private ElasticSQLResultSet equalsSelectionProcess(String key, String data, ElasticSQLResultSet nrs) throws Exception {
        ArrayList<String> _id = new ArrayList<>();
        ArrayList<String> _data = new ArrayList<>();
        ArrayList<String> _keys = new ArrayList<>();

        JSONParser parser = new JSONParser();

        while(nrs.next()) {
            String innerJSON = nrs.getEntireData();
            JSONObject jsonData = (JSONObject) parser.parse(innerJSON);
            String innerData = jsonData.get(key).toString();
            if (innerData.equals(data)) {
                _id.add(nrs.getID());
                _data.add(nrs.getEntireData());
                _keys.add(nrs.getIndex());
            }
        }

        return new ElasticSQLResultSet(_id, _data, _keys);
    }

    public ElasticSQLResultSet selectSimilar(String key, String data) throws Exception {
        ElasticSQLResultSet nrs = selectWhereKey(key);
        return similarSelectionProcess(key, data, nrs);
    }

    public ElasticSQLResultSet selectSimilar(String key, String data, int limit) throws Exception {
        ElasticSQLResultSet nrs = selectWhereKey(key, limit);
        return similarSelectionProcess(key, data, nrs);
    }

    private ElasticSQLResultSet similarSelectionProcess(String key, String data, ElasticSQLResultSet nrs) throws Exception {
        JSONParser parser = new JSONParser();

        ArrayList<String> _id = new ArrayList<>();
        ArrayList<String> _data = new ArrayList<>();
        ArrayList<String> _keys = new ArrayList<>();

        while(nrs.next()) {
            String innerJSON = nrs.getEntireData();
            JSONObject jsonData = (JSONObject) parser.parse(innerJSON);
            String innerData = jsonData.get(key).toString();
            if (innerData.contains(data)) {
                _id.add(nrs.getID());
                _data.add(nrs.getEntireData());
                _keys.add(nrs.getIndex());
            }
        }

        return new ElasticSQLResultSet(_id, _data, _keys);
    }

    public ElasticSQLResultSet selectAt(String id) throws Exception {
        return new ElasticSQLResultSet(controller.getRow(ElasticSQLResultSet.COLUMN_ID, id));
    }

    public ElasticSQLResultSet selectWhereKey(String key) throws Exception {
        return new ElasticSQLResultSet(controller.getSimilarRow(ElasticSQLResultSet.COLUMN_INDEX, key + ","));
    }

    public ElasticSQLResultSet selectWhereKey(String key, int limit) throws Exception {
        return new ElasticSQLResultSet(controller.getSimilarRow(ElasticSQLResultSet.COLUMN_INDEX, key + ",", limit));
    }

    public void insert(String[] keys, String[] data) throws Exception {
        JSONObject jsonData = new JSONObject();
        String allIndex = "";
        for(int i = 0; i < keys.length; i++) {
            if (keys[i].contains(",")) {
                throw new ElasticSQLException("Including comma (,) in key is not allowed.");
            }
            jsonData.put(keys[i], data[i]);
            allIndex += keys[i] + ",";
        }
        String[] columns = {ElasticSQLResultSet.COLUMN_DATA, ElasticSQLResultSet.COLUMN_INDEX};
        String[] datas = {jsonData.toString().replace("\"", "\\\""), allIndex};
        controller.insertRow(columns, datas);
    }

    public void update(String selectionKey, String selectionValue, String updateKey, String newValue, int limit) throws Exception {
        ElasticSQLResultSet nrs = select(selectionKey, selectionValue, limit);
        updateProc(updateKey, newValue, nrs);
    }

    public void update(String selectionKey, String selectionValue, String updateKey, String newValue) throws Exception {
        ElasticSQLResultSet nrs = select(selectionKey, selectionValue);
        updateProc(updateKey, newValue, nrs);
    }

    private void updateProc(String key, String newValue, ElasticSQLResultSet nrs) throws ElasticSQLException, ParseException, SQLException {
        while(nrs.next()) {
            String localID = nrs.getID();
            String localData = nrs.getEntireData();

            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(localData);

            obj.remove(key);
            obj.put(key, newValue);

            controller.updateRow(ElasticSQLResultSet.COLUMN_ID, localID, ElasticSQLResultSet.COLUMN_DATA, obj.toString().replace("\"", "\\\""), 1);
        }
    }

    public void addData(String selectionKey, String selectionValue, String newKey, String newValue, int limit) throws Exception {
        ElasticSQLResultSet nrs = select(selectionKey, selectionValue, limit);
        while(nrs.next()) {
            addData(nrs.getID(), newKey, newValue);
        }
    }

    public void addData(String selectionKey, String selectionValue, String newKey, String newValue) throws Exception {
        ElasticSQLResultSet nrs = select(selectionKey, selectionValue);
        while(nrs.next()) {
            addData(nrs.getID(), newKey, newValue);
        }
    }

    public void addData(String selectionID, String newKey, String newValue) throws Exception {
        ElasticSQLResultSet nrs = new ElasticSQLResultSet(controller.getRow(ElasticSQLResultSet.COLUMN_ID, selectionID));
        if (!nrs.next()) {
            throw new ElasticSQLException("Unable to find element of ID " + selectionID + ".");
        }
        if (nrs.getIndex().contains("," + newKey + ",") || nrs.getIndex().startsWith(newKey + ",")) {
            throw new ElasticSQLException("Element of ID " + nrs.getID() + " already has key of \"" + newKey + "\".");
        }
        if (newKey.contains(",")) {
            throw new ElasticSQLException("Including comma (,) in key is not allowed.");
        }
        String newKeysIndex = nrs.getIndex() + newKey + ",";
        String newJSON = nrs.getEntireData();
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(newJSON);
        json.put(newKey, newValue);
        newJSON = json.toString();
        controller.updateRow(ElasticSQLResultSet.COLUMN_ID, selectionID, ElasticSQLResultSet.COLUMN_DATA, newJSON.replace("\"", "\\\""));
        controller.updateRow(ElasticSQLResultSet.COLUMN_ID, selectionID, ElasticSQLResultSet.COLUMN_INDEX, newKeysIndex);
    }

    public void popData(String selectionKey, String selectionValue, String popKey, int limit) throws Exception {
        ElasticSQLResultSet nrs = select(selectionKey, selectionValue, limit);
        while(nrs.next()) {
            popData(nrs.getID(), popKey);
        }
    }

    public void popData(String selectionKey, String selectionValue, String popKey) throws Exception {
        ElasticSQLResultSet nrs = select(selectionKey, selectionValue);
        while(nrs.next()) {
            popData(nrs.getID(), popKey);
        }
    }

    public void popData(String selectionID, String popKey) throws Exception {
        ElasticSQLResultSet nrs = new ElasticSQLResultSet(controller.getRow(ElasticSQLResultSet.COLUMN_ID, selectionID));
        if (!nrs.next()) {
            throw new ElasticSQLException("Unable to find element of ID " + selectionID + ".");
        }
        if (!nrs.getIndex().contains("," + popKey + ",") && !nrs.getIndex().startsWith(popKey + ",")) {
            throw new ElasticSQLException("Element of ID " + nrs.getID() + " does not have key of \"" + popKey + "\".");
        }
        String newKeysIndex = nrs.getIndex().replace( popKey + ",", "");
        String newJSON = nrs.getEntireData();
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(newJSON);
        json.remove(popKey);
        newJSON = json.toString();
        controller.updateRow(ElasticSQLResultSet.COLUMN_ID, selectionID, ElasticSQLResultSet.COLUMN_DATA, newJSON.replace("\"", "\\\""));
        controller.updateRow(ElasticSQLResultSet.COLUMN_ID, selectionID, ElasticSQLResultSet.COLUMN_INDEX, newKeysIndex);
    }

    public void delete(String key, String data) throws Exception {
        ElasticSQLResultSet nrs = select(key, data);
        while(nrs.next()) {
            controller.deleteRow(ElasticSQLResultSet.COLUMN_ID, nrs.getID());
        }
    }

    public void delete(String key, String data, int limit) throws Exception {
        ElasticSQLResultSet nrs = select(key, data, limit);
        while(nrs.next()) {
            controller.deleteRow(ElasticSQLResultSet.COLUMN_ID, nrs.getID(), limit);
        }
    }

    public void delete(String selectionID) throws Exception {
        ElasticSQLResultSet nrs = select(ElasticSQLResultSet.COLUMN_ID, selectionID);
        while(nrs.next()) {
            controller.deleteRow(ElasticSQLResultSet.COLUMN_ID, nrs.getID());
        }
    }
}
