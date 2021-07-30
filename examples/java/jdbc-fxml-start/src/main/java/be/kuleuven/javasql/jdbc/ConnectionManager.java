package be.kuleuven.javasql.jdbc;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private Connection connection;
    public static final String ConnectionString = "jdbc:sqlite:mydb.db";

    public Connection getConnection() {
        return connection;
    }

    public void flushConnection() {
        try {
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ConnectionManager() {
        try {
            // auto-creates if not exists
            connection = DriverManager.getConnection(ConnectionString);
            connection.setAutoCommit(false);

            initTables();
            verifyTableContents();
        } catch (Exception e) {
            System.out.println("Db connection handle failure");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void verifyTableContents() throws SQLException {
        var s = connection.createStatement();
        var result = s.executeQuery("SELECT COUNT(*) as cnt FROM student");
        assert result.getInt("cnt") == 3;
    }

    private void initTables() throws Exception {
        var sql = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("dbcreate.sql").getPath())));
        System.out.println(sql);

        var s = connection.createStatement();
        s.executeUpdate(sql);
        s.close();
    }


}
