package be.kuleuven.studenthibernate.domain;

import javax.persistence.EntityManager;
import java.util.List;

public class StudentRepositoryJpaImpl implements StudentRepository {

    private final EntityManager entityManager;

    public StudentRepositoryJpaImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Student> getStudentsByName(String student) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void saveNewStudent(Student student) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void updateStudent(Student student) {
        throw new UnsupportedOperationException("TODO");
    }
}
