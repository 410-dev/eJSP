package ejsp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseController {

    public Connection connection = null;

    public boolean serverStarted = false;
    public boolean closeRefresherThread = false;

    private String protocol = null;
    private String address = null;
    private String port = null;
    private String databaseName = null;
    private String username = null;
    private String password = null;
    private String driverName = null;

    private Thread queryRefresher = null;

    public DatabaseController(String protocol, String address, String port, String databaseName, String username, String password, String driverName) {
        this.protocol = protocol;
        this.address = address;
        this.port = port;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
        this.driverName = driverName;
    }

    public DatabaseController(String databaseName, String username, String password) {
        this.protocol = "jdbc:mariadb";
        this.address = "localhost";
        this.port = "3306";
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
        this.driverName = "org.mariadb.jdbc.Driver";
    }

    private void refreshQuery() {
        if (!serverStarted) {
            serverStarted = true;
            closeRefresherThread = false;
            queryRefresher = new Thread(() -> {
                try {
                    while (!closeRefresherThread) {
                        if (connection != null) connection.close();
                        connection = null;
                        final String ADDRESS = protocol + "://" + address + ":" + port + "/" + databaseName;
                        Class.forName(driverName);
                        connection = DriverManager.getConnection(ADDRESS, username, password);
                        executeQuery("");
                        Thread.sleep(3600000);
                    }
                } catch (Exception e) {
                    if (!e.toString().contains("InterruptedException")) {
                        e.printStackTrace();
                    }
                }
            });
            queryRefresher.start();
        }
    }

    public ResultSet executeQuery(String toExecute) throws SQLException {
        refreshQuery();
        return connection.prepareStatement(toExecute).executeQuery();
    }

    public void clearControllerThread() {
        closeRefresherThread = true;
        queryRefresher.interrupt();
    }

    public ResultSet getAllRows(String tableName) throws SQLException {
        return executeQuery("select * from " + tableName + ";");
    }

    public ResultSet getRow(String tableName, String columnID, String value) throws SQLException {
        return executeQuery("select * from " + tableName + " where " + columnID + " = \"" + value + "\";");
    }

    public void insertRow(String tableName, String[] columnIDs, String[] values) throws SQLException {
        String valueString = "";
        for(int i = 0; i < values.length; i++) valueString += "\"" + values[i] + "\", ";
        executeQuery("insert into " + tableName + "(" + String.join(", ", columnIDs) + ") values(" + valueString + ");");
    }

    public void updateRow(String tableName, String targetColumnID, String targetValue, String columnID, String value) throws SQLException {
        executeQuery("update " + tableName + " set " + columnID + "=\"" + value + "\" where " + targetColumnID + "=\"" + targetValue + "\";");
    }

    public void deleteRow(String tableName, String targetColumnID, String targetValue) throws SQLException {
        executeQuery("delete from " + tableName + " where " + targetColumnID + " = \"" + targetValue + "\";");
    }
}
