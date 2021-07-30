package be.kuleuven.javasql.jdbi;

import be.kuleuven.javasql.domain.Student;
import be.kuleuven.javasql.domain.StudentRepository;

import java.util.List;

public class StudentRepositoryJdbi3Impl implements StudentRepository {

    public StudentRepositoryJdbi3Impl(String connectionString) {
        // TODO
    }

    @Override
    public List<Student> getStudentsByName(String student) {
        // TODO
        return null;
    }

    @Override
    public void saveNewStudent(Student student) {
        // TODO
    }

    @Override
    public void updateStudent(Student student) {
        // TODO
    }
}
