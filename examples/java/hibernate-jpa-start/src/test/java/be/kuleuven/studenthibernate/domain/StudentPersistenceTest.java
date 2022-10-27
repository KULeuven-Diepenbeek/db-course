package be.kuleuven.studenthibernate.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class StudentPersistenceTest {

    private EntityManagerFactory factory;
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        this.factory = Persistence.createEntityManagerFactory("be.kuleuven.studenthibernate.domain");
        this.entityManager = factory.createEntityManager();
    }

    @AfterEach
    public void tearDown() {
        factory.close();
    }

    @Test
    public void studentCanBePersisted() {
        var jos = new Student("Lowiemans", "Jos", true);
        entityManager.persist(jos);
    }

}
