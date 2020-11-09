package be.kuleuven.concurrency;

import be.kuleuven.concurrency.chaos.StudentAdder;
import be.kuleuven.concurrency.chaos.StudentDeleter;
import be.kuleuven.concurrency.chaos.StudentUpdater;
import be.kuleuven.concurrency.jdbc.ConnectionManager;
import be.kuleuven.concurrency.jdbc.StudentRepositoryJdbcImpl;

import java.sql.SQLException;

public class ConcurrencyMain {

    public static void main(String[] args) throws InterruptedException, SQLException {
        ConnectionManager connectionManager = new ConnectionManager();
        var jdbcRepo = new StudentRepositoryJdbcImpl(connectionManager.getConnection());

        //connectionManager.getConnection().setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

        for(var i = 0; i < 10; i++) {
            Thread thread = new Thread(new StudentAdder(jdbcRepo, i), "adder " + i);
            thread.setDaemon(true);
            thread.start();
        }
        for(var i = 0; i < 4; i++) {
            Thread thread = new Thread(new StudentDeleter(jdbcRepo, i), "deleter " + i);
            thread.setDaemon(true);
            thread.start();
        }
        for(var i = 0; i < 4; i++) {
            Thread thread = new Thread(new StudentUpdater(jdbcRepo, i), "updater " + i);
            thread.setDaemon(true);
            thread.start();
        }

        Thread.sleep(2000);
        printStudents(jdbcRepo);
    }

    private static void printStudents(StudentRepositoryJdbcImpl jdbcRepo) {
        for(var student : jdbcRepo.getAllStudents()) {
            System.out.println("Found student " + student);
        }
    }

}
