package be.kuleuven.studenthibernate.domain;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class StudentPersistenceTest {

    private EntityManagerFactory factory;
    private EntityManager entityManager;

    @Before
    public void setUp() {
        this.factory = Persistence.createEntityManagerFactory("be.kuleuven.studenthibernate.domain");
        this.entityManager = factory.createEntityManager();
    }

    @After
    public void tearDown() {
        factory.close();
    }

    @Test
    public void studentCanBePersisted() {
        var jos = new Student("Lowiemans", "Jos", true);
        entityManager.persist(jos);
    }

}
