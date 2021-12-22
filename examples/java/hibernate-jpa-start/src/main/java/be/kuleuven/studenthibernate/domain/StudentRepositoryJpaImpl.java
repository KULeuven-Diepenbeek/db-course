package be.kuleuven.studenthibernate.domain;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

public class StudentRepositoryJpaImpl implements StudentRepository {

    private final EntityManager entityManager;

    public StudentRepositoryJpaImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Student> getStudentsByName(String studentenNaam) {

        var criteriaBuilder = entityManager.getCriteriaBuilder();
        var query = criteriaBuilder.createQuery(Student.class); // SELECT ... FROM STUDENT
        var root = query.from(Student.class); // SELECT *

        query.where(criteriaBuilder.equal(root.get("naam"),studentenNaam));

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
        entityManager.getTransaction().begin();
        entityManager.merge(student);
        entityManager.getTransaction().commit();
    }
}
