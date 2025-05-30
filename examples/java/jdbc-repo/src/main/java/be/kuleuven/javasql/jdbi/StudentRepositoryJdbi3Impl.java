package be.kuleuven.javasql.jdbi;

import be.kuleuven.javasql.Student;
import be.kuleuven.javasql.StudentRepository;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class StudentRepositoryJdbi3Impl implements StudentRepository {

    private Jdbi jdbi;

    public StudentRepositoryJdbi3Impl(String connectionString) {
        jdbi = Jdbi.create(connectionString);
    }

    @Override
    public List<Student> getStudentsByName(String student) {
        return jdbi.withHandle(handle -> {
           return handle.createQuery("SELECT * FROM student WHERE naam = :naam")
                   .bind("naam", student)
                   .mapToBean(Student.class)
                   .list();
        });

    }

    @Override
    public void saveNewStudent(Student student) {
        jdbi.withHandle(handle -> {
            //handle.execute("INSERT INTO student (studnr, naam, voornaam, goedBezig) VALUES (?, ?, ?, ?)",
            //        student.getStudnr(), student.getNaam(), student.getVoornaam(), student.isGoedBezig());

            return handle.createUpdate("INSERT INTO student (studnr, naam, voornaam, goedBezig) VALUES (:studnr, :naam, :voornaam, :goedBezig)")
                    .bindBean(student)
                    .execute();
        });
    }

    @Override
    public void updateStudent(Student student) {
    }
}
