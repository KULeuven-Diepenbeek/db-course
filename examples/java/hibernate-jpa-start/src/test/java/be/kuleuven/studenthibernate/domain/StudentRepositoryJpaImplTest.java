package be.kuleuven.studenthibernate.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class StudentRepositoryJpaImplTest {

    private EntityManagerFactory factory;
    private StudentRepository studentRepository;
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        this.factory = Persistence.createEntityManagerFactory("be.kuleuven.studenthibernate.domain");
        this.entityManager = factory.createEntityManager();

        studentRepository = new StudentRepositoryJpaImpl(entityManager);
    }

    @AfterEach
    public void tearDown() {
        factory.close();
    }

    @Test
    public void saveNewStudent_newStudentSavedInDb() {
        var jos = new Student("Lowiemans", "Jos", true);
        studentRepository.saveNewStudent(jos);

        Assertions.assertTrue(jos.getStudentenNummer() > 0, "student heeft ID gekregen");
    }

    @Test
    public void updateStudent_studentNaamEnVoornaamGewijzigd() {
        var jos = new Student("Lowiemans", "Jos", true);
        studentRepository.saveNewStudent(jos);
        entityManager.clear();

        jos.setNaam("Lowiemans2");
        jos.setVoornaam("Josette");
        studentRepository.updateStudent(jos);
        entityManager.clear();

        var studenten = studentRepository.getStudentsByName("Lowiemans2");
        Assertions.assertTrue(studenten.size() == 1, "student lowiemans2 is in db");
        var lowiemans2 = studenten.get(0);
        Assertions.assertEquals("Josette", lowiemans2.getVoornaam(), "ook voornaam gewijzigd");
    }

    @Test
    public void TODO_Schrijf_Meer_Testen() {
        Assertions.fail("Zie methodenaam!");
    }

}
