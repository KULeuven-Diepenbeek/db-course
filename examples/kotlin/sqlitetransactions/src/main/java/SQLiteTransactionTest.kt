import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


class SQLiteTransactionTest {
    private var connection: Connection

    constructor() {
        try {
            // auto-creates if not exists
            connection = DriverManager.getConnection("jdbc:sqlite:mydb.db").apply {
                autoCommit = false
            }
            initTables()
            verifyTableContents()
        } catch (e: Exception) {
            println("Db connection handle failure")
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }

    private fun verifyTableContents() {
        val s = connection.createStatement()
        val result = s.executeQuery("SELECT COUNT(*) as cnt FROM student")
        assert(result.getInt("cnt") === 3)
    }

    private fun initTables() {
        val sql = javaClass.getResource("dbcreate.sql").readText()
        println(sql)
        with(connection.createStatement()) {
            executeUpdate(sql)
            close()
        }
    }

    fun doStuff() {
        val sql = """
             UPDATE student SET voornaam = 'Jaqueline' WHERE studnr = 123;
             INSERT INTO oeitiskapot;
             INSERT INTO log(foreign_id, msg) VALUES (123, 'Voornaam vergissing');
             INSERT INTO student(studnr, naam, voornaam, goedbezig) VALUES (445, 'Klakmans', 'Jef', 1);
             INSERT INTO log(foreign_id, msg) VALUES (445, 'Nieuwe student registratie');
             """.trimIndent()
        try {
            val s = connection.createStatement()
            with(s) {
                executeUpdate(sql)
                close()
            }
        } catch (ex: SQLException) {
            // whoops. Something went wrong.
            println("Something went wrong (as expected) while updating student naam")
            ex.printStackTrace()
            try {
                connection.rollback()
            } catch (e: SQLException) {
                throw RuntimeException(e)
            }
            verifyStudentNaam(123, "Jaak")
        }
    }

    private fun verifyStudentNaam(studnr: Int, naam: String) {
        connection.createStatement().use {
            val result = it.executeQuery("SELECT voornaam FROM student WHERE studnr = 123")
            val dbvoornaam = result.getString("voornaam")
            if (naam != dbvoornaam) {
                throw InvalidDBStateException("Expected DB naam to be $naam but got $dbvoornaam")
            }
        }
    }
}
