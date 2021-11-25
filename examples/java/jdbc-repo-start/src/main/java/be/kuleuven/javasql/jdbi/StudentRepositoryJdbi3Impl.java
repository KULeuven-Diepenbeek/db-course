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
        throw new UnsupportedOperationException("TODO Implement me!");
    }

    @Override
    public void saveNewStudent(Student student) {
        throw new UnsupportedOperationException("TODO Implement me!");
    }

    @Override
    public void updateStudent(Student student) {
        throw new UnsupportedOperationException("TODO Implement me!");
    }
}
