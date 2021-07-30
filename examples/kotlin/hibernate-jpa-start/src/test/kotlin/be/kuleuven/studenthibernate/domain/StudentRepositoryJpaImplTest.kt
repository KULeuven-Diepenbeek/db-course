package be.kuleuven.studenthibernate.domain

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence
import kotlin.test.fail

class StudentRepositoryJpaImplTest {

    private lateinit var factory: EntityManagerFactory
    private lateinit var entityManager: EntityManager
    private lateinit var studentRepository: StudentRepository

    @BeforeEach
    fun setUp() {
        factory = Persistence.createEntityManagerFactory("be.kuleuven.studenthibernate.domain")
        entityManager = factory.createEntityManager()

        studentRepository = StudentRepositoryJpaImpl(entityManager)
    }

    @AfterEach
    fun tearDown() {
        factory.close()
    }

    @Test
    fun saveNewStudent_newStudentSavedInDb() {
        val jos = Student("Lowiemans", "Jos", true)
        studentRepository.saveNewStudent(jos)
        assertTrue(jos.studentenNummer > 0, "student heeft ID gekregen")
    }

    @Test
    fun updateStudent_studentNaamEnVoornaamGewijzigd() {
        val jos = Student("Lowiemans", "Jos", true)
        studentRepository.saveNewStudent(jos)
        entityManager.clear()
        with(jos) {
            naam = "Lowiemans2"
            voornaam = "Josette"
        }
        studentRepository.updateStudent(jos)
        entityManager.clear()

        val studenten = studentRepository.getStudentsByName("Lowiemans2")
        assertTrue(studenten.size == 1, "student lowiemans2 is in db")
        assertEquals("ook voornaam gewijzigd", "Josette", studenten[0].voornaam)
    }

    @Test
    fun TODO_Schrijf_Meer_Testen() {
        fail("Zie methodenaam!")
    }}
