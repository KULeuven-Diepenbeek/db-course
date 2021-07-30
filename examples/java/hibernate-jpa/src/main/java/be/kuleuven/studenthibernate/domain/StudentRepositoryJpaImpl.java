package be.kuleuven.studenthibernate.domain;

import javax.persistence.EntityManager;
import javax.persistence.PrePersist;
import java.util.List;

public class StudentRepositoryJpaImpl implements StudentRepository {

    private final EntityManager entityManager;

    public StudentRepositoryJpaImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Student> getStudentsByName(String student) {
        var criteriaBuilder = entityManager.getCriteriaBuilder();
        var query = criteriaBuilder.createQuery(Student.class);
        var root = query.from(Student.class);

        query.where(criteriaBuilder.equal(root.get("naam"), student));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public void saveNewStudent(Student student) {
        entityManager.getTransaction().begin();
        entityManager.persist(student);
        entityManager.getTransaction().commit();
    }

    @Override
    public void updateStudent(Student student) {
        entityManager.merge(student);
    }
}
