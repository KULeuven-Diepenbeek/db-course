import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteTransactionTest {

    private Connection connection;

    public SQLiteTransactionTest() {
        try {
            // auto-creates if not exists
            connection = DriverManager.getConnection("jdbc:sqlite:mydb.db");
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
        var sql = new String(Files.readAllBytes(Paths.get(getClass().getResource("dbcreate.sql").getPath())));
        System.out.println(sql);

        var s = connection.createStatement();
        s.executeUpdate(sql);
        s.close();
    }

    public void doStuff() {
        var sql = "UPDATE student SET voornaam = 'Jaqueline' WHERE studnr = 123;\n" +
                "INSERT INTO oeitiskapot;\n" +
                "INSERT INTO log(foreign_id, msg) VALUES (123, 'Voornaam vergissing');\n" +
                "INSERT INTO student(studnr, naam, voornaam, goedbezig) VALUES (445, 'Klakmans', 'Jef', 1);\n" +
                "INSERT INTO log(foreign_id, msg) VALUES (445, 'Nieuwe student registratie');";

        try {
            var s = connection.createStatement();

            s.executeUpdate(sql);
            s.close();
        } catch(SQLException ex) {
            // whoops. Something went wrong.
            System.out.println("Something went wrong (as expected) while updating student naam");
            ex.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            verifyStudentNaam(123, "Jaak");
        }
    }

    private void verifyStudentNaam(int studnr, String naam) {
        try {
            var s2 = connection.createStatement();
            var result = s2.executeQuery("SELECT voornaam FROM student WHERE studnr = 123");
            var dbvoornaam = result.getString("voornaam");
            if(!naam.equals(dbvoornaam)) {
                throw new InvalidDBStateException("Expected DB naam to be " + naam + " but got " + dbvoornaam);
            }
            s2.close();
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
