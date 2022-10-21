package be.kuleuven.javasql.jdbc;

import be.kuleuven.javasql.BaseStudentRepositoryTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.SQLException;

public class StudentRepositoryJdbcImplTest extends BaseStudentRepositoryTest {

    private Connection jdbcConnection;

    @BeforeEach
    public void setUp() {
        this.jdbcConnection = new ConnectionManager().getConnection();
        this.studentRepository = new StudentRepositoryJdbcImpl(this.jdbcConnection);
    }

    @AfterEach
    public void tearDown() {
        try {
            this.jdbcConnection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
