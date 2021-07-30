package be.kuleuven.javasql;

import be.kuleuven.javasql.jdbc.ConnectionManager;
import be.kuleuven.javasql.jdbc.StudentRepositoryJdbcImpl;
import be.kuleuven.javasql.jdbi.StudentRepositoryJdbi3Impl;

public class RepoMain {

    public static void main(String[] args) {
        ConnectionManager connectionManager = new ConnectionManager();
        var jdbcRepo = new StudentRepositoryJdbcImpl(connectionManager.getConnection());
        var jdbiRepo = new StudentRepositoryJdbi3Impl(ConnectionManager.ConnectionString);
        doStuff(jdbcRepo, "JDBC");

        connectionManager.flushConnection();
        doStuff(jdbiRepo, "JDBI 3");
    }

    private static void doStuff(StudentRepository repo, String impl) {
        System.out.println("---- with SQL impl: " + impl + "\n");
        System.out.println("We vragen studenten op die 'Peeters' heten...");
        for (var student : repo.getStudentsByName("Peeters")) {
            System.out.println(student);
        }

        System.out.println("Saving new student...");
        var newStudent = new Student("Kloostermans", "Sarah", 666, true);
        repo.saveNewStudent(newStudent);

        System.out.println("We vragen studenten op die 'Kloostermans' (nieuwe student) heten...");
        for (var student : repo.getStudentsByName("Kloostermans")) {
            System.out.println(student);
        }
    }
}
