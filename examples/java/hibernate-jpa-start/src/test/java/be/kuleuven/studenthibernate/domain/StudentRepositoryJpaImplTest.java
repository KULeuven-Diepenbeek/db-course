package be.kuleuven.studenthibernate.domain;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class StudentRepositoryJpaImplTest {

    private EntityManagerFactory factory;
    private StudentRepository studentRepository;
    private EntityManager entityManager;

    @Before
    public void setUp() {
        this.factory = Persistence.createEntityManagerFactory("be.kuleuven.studenthibernate.domain");
        this.entityManager = factory.createEntityManager();

        studentRepository = new StudentRepositoryJpaImpl(entityManager);
    }

    @After
    public void tearDown() {
        factory.close();
    }

    @Test
    public void saveNewStudent_newStudentSavedInDb() {
        var jos = new Student("Lowiemans", "Jos", true);
        studentRepository.saveNewStudent(jos);

        Assert.assertTrue("student heeft ID gekregen", jos.getStudentenNummer() > 0);
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
        Assert.assertTrue("student lowiemans2 is in db", studenten.size() == 1);
        var lowiemans2 = studenten.get(0);
        Assert.assertEquals("ook voornaam gewijzigd", "Josette", lowiemans2.getVoornaam());
    }

    @Test
    public void TODO_Schrijf_Meer_Testen() {
        Assert.fail("Zie methodenaam!");
    }

}
