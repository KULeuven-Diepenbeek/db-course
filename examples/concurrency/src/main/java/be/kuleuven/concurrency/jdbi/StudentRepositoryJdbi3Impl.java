package be.kuleuven.concurrency.jdbi;

import be.kuleuven.concurrency.Student;
import be.kuleuven.concurrency.StudentRepository;
import org.jdbi.v3.core.Jdbi;

import java.util.ArrayList;
import java.util.List;

public class StudentRepositoryJdbi3Impl implements StudentRepository {

    private Jdbi jdbi;

    public StudentRepositoryJdbi3Impl(String connectionString) {
        jdbi = Jdbi.create(connectionString);
    }

    @Override
    public List<Student> getAllStudents() {
        return new ArrayList<>();
    }

    @Override
    public void saveNewStudent(Student student) {

    }

    @Override
    public void updateStudent(Student student) {

    }

    @Override
    public void deleteStudent(Student student) {

    }
}
