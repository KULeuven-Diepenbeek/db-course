package be.kuleuven.studenthibernate.domain

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence

class StudentPersistenceTest {

    private lateinit var factory: EntityManagerFactory
    private lateinit var entityManager: EntityManager

    @BeforeEach
    fun setUp() {
        factory = Persistence.createEntityManagerFactory("be.kuleuven.studenthibernate.domain")
        entityManager = factory.createEntityManager()
    }

    @AfterEach
    fun tearDown() {
        factory.close()
    }

    @Test
    fun studentCanBePersisted() {
        val jos = Student("Lowiemans", "Jos", true)
        entityManager.persist(jos)
    }
}