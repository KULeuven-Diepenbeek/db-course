package be.kuleuven.concurrency.jdbc;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;

public class ConnectionManager {
    public static final String ConnectionStringSQLite = "jdbc:sqlite:mydb.db";
    public static final String ConnectionStringH2 = "jdbc:h2:./mydb.h2db";

    public static void initTables(Connection connection) throws Exception {
        var uri = ConnectionManager.class.getResource("/dbcreate.sql").toURI();
        var sql = new String(Files.readAllBytes(Paths.get(uri)));
        System.out.println(sql);

        var s = connection.createStatement();
        s.executeUpdate(sql);
        s.close();
    }


}
