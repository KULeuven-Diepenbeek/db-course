package be.kuleuven.concurrency;

import be.kuleuven.concurrency.jdbc.ConnectionManager;
import be.kuleuven.concurrency.jdbc.StudentRepositoryJdbcImpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConcurrencyMain {

    public static void main(String[] args) throws Exception {
        // For testing purposes, you can revert to SQLite to see why this (doesn't) work
        // Just use ConnectionManager.ConnectionStringSQLite instead.
        var connection = DriverManager.getConnection(ConnectionManager.ConnectionStringH2);
        connection.setAutoCommit(false);
        ConnectionManager.initTables(connection);
        connection.commit();

        var student = new Student("Groeneveld", "Wouter", 1245, true);
        new StudentRepositoryJdbcImpl(connection).saveNewStudent(student);
         connection.commit();

        new Thread(() -> {
            try {
                // simulate dirty read: get another connection
                var otherConnection = DriverManager.getConnection(ConnectionManager.ConnectionStringH2);
                otherConnection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                //otherConnection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                otherConnection.setAutoCommit(true);

                System.out.println(" ## Trying to DIRTY read students: ");
                printStudents(new StudentRepositoryJdbcImpl(otherConnection));
                otherConnection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).run();

        Thread.sleep(2000);
//        connection.rollback();
//        connection.commit();

        System.out.println(" ## Re-reading students AFTER rollback: ");
        printStudents(new StudentRepositoryJdbcImpl(connection));


        System.out.println(" -- DONE");
    }

    private static void printStudents(StudentRepositoryJdbcImpl jdbcRepo) {
        System.out.println(" -- Printing students ->");
        for(var student : jdbcRepo.getAllStudents()) {
            System.out.println("Found student " + student);
        }
    }

}
