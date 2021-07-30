package be.kuleuven.javasql.jdbc;

import be.kuleuven.javasql.BaseStudentRepositoryTest;
import org.junit.After;
import org.junit.Before;

import java.sql.Connection;
import java.sql.SQLException;

public class StudentRepositoryJdbcImplTest extends BaseStudentRepositoryTest {

    private Connection jdbcConnection;

    @Before
    public void setUp() {
        this.jdbcConnection = new ConnectionManager().getConnection();
        this.studentRepository = new StudentRepositoryJdbcImpl(this.jdbcConnection);
    }

    @After
    public void tearDown() {
        try {
            this.jdbcConnection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
