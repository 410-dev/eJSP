package ejsp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLController {

    private final String protocol;
    private final String address;
    private final String port;
    private final String databaseName;
    private final String username;
    private final String password;
    private final String driverName;
    private final String tableName;

    public SQLController(String protocol, String address, String port, String databaseName, String tableName, String username, String password, String driverName) {
        this.protocol = protocol;
        this.address = address;
        this.port = port;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
        this.driverName = driverName;
        this.tableName = tableName;
    }

    public SQLController(String databaseName, String tableName, String username, String password) {
        this.protocol = "jdbc:mariadb";
        this.address = "localhost";
        this.port = "3306";
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
        this.driverName = "org.mariadb.jdbc.Driver";
        this.tableName = tableName;
    }

    public ResultSet executeQuery(String toExecute) throws SQLException {
        ResultSet toReturn;
        try {
            final String ADDRESS = protocol + "://" + address + ":" + port + "/" + databaseName;
            Class.forName(driverName);
            Connection connection = DriverManager.getConnection(ADDRESS, username, password);
            toReturn = connection.prepareStatement(toExecute).executeQuery();
            connection.close();
        }catch(ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return toReturn;
    }

    public ResultSet getAllRows() throws SQLException {
        return executeQuery("select * from " + tableName + ";");
    }

    public ResultSet getAllRows(int limit) throws SQLException {
        return executeQuery("select * from " + tableName + " limit " + limit + ";");
    }

    public ResultSet getAllRows(String column, int limit) throws SQLException {
        return executeQuery("select " + column + " from " + tableName + " limit " + limit + ";");
    }

    public ResultSet getRow(String column, String value) throws SQLException {
        return executeQuery("select * from " + tableName + " where " + column + " = \"" + value + "\";");
    }

    public ResultSet getRow(String column, String value, int limit) throws SQLException {
        return executeQuery("select * from " + tableName + " where " + column + " = \"" + value + "\" limit " + limit + ";");
    }

    public ResultSet getSimilarRow(String column, String value) throws SQLException {
        return executeQuery("select * from " + tableName + " where " + column + " like \"%" + value + "%\";");
    }

    public ResultSet getSimilarRow(String column, String value, int limit) throws SQLException {
        return executeQuery("select * from " + tableName + " where " + column + " like \"%" + value + "%\" limit " + limit + ";");
    }

    public ResultSet getColumnOfRow(String columnToGet, String searchColumn, String value) throws SQLException {
        return executeQuery("select " + columnToGet + " from " + tableName + " where " + searchColumn + " = \"" + value + "\";");
    }

    public ResultSet getColumnOfRow(String columnToGet, String searchColumn, String value, int limit) throws SQLException {
        return executeQuery("select " + columnToGet + " from " + tableName + " where " + searchColumn + " = \"" + value + "\" limit " + limit + ";");
    }

    public void insertRow(String[] columnIDs, String[] values) throws SQLException {
        String valueString = "";
        for(int i = 0; i < values.length; i++)  {
            if (!values[i].startsWith("!INT")) {
                valueString += "\"" + values[i] + "\", ";
            }else{
                valueString += values[i].replace("!INT", "") + ", ";
            }
        }
        String columns = String.join(", ", columnIDs);
        if (columns.startsWith(", ")) columns = columns.substring(1);
        if (columns.endsWith(", ")) columns = columns.substring(0, columns.length()-2);

        if (valueString.startsWith(", ")) valueString = valueString.substring(1);
        if (valueString.endsWith(", ")) valueString = valueString.substring(0, valueString.length()-2);
        executeQuery("insert into " + tableName + "(" + columns + ") values(" + valueString + ");");
    }

    public void updateRow(String targetColumn, String targetValue, String column, String value) throws SQLException {
        executeQuery("update " + tableName + " set " + column + "=\"" + value + "\" where " + targetColumn + "=\"" + targetValue + "\";");
    }

    public void updateRow(String targetColumn, String targetValue, String column, String value, int limit) throws SQLException {
        executeQuery("update " + tableName + " set " + column + "=\"" + value + "\" where " + targetColumn + "=\"" + targetValue + "\" limit " + limit + ";");
    }

    public void deleteRow(String targetColumn, String targetValue) throws SQLException {
        executeQuery("delete from " + tableName + " where " + targetColumn + " = \"" + targetValue + "\";");
    }

    public void deleteRow(String targetColumn, String targetValue, int limit) throws SQLException {
        executeQuery("delete from " + tableName + " where " + targetColumn + " = \"" + targetValue + "\" limit " + limit + ";");
    }

}
