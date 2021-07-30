package be.kuleuven.javasql;

import be.kuleuven.javasql.domain.InvalidStudentException;
import be.kuleuven.javasql.domain.Student;
import be.kuleuven.javasql.domain.StudentRepository;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public abstract class BaseStudentRepositoryTest {

    protected StudentRepository studentRepository;

    @Test
    public void getStudentsByName_NameUnknown_ReturnsEmptyList() {
        List<Student> result = studentRepository.getStudentsByName("bloekiebla");
        Assert.assertNotNull("result should not be null", result);
        Assert.assertTrue("resultset should be zero", result.size() == 0);
    }

    @Test
    public void getStudentsByName_NameKnownInDb_ReturnsListWithRecord() {
        // We weten dat student "Peeters" reeds in de DB zit.

        List<Student> result = studentRepository.getStudentsByName("Peeters");
        Assert.assertNotNull("result should not be null", result);
        Assert.assertTrue("resultset should be one record", result.size() == 1);
        Assert.assertEquals("Name should be Peeters", "Peeters", result.get(0).getNaam());
    }

    @Test(expected = RuntimeException.class)
    public void saveStudent_samePrimaryKey_ShouldCrash() {
        // We weten dat student "Peeters" reeds in de DB zit met als key 456.
        studentRepository.saveNewStudent(new Student("PeetersCopy", "Jozefien", 456, true));
    }

    @Test(expected = InvalidStudentException.class)
    public void updateStudent_unknownStudnr_throwsInvalidStudentException() {
        var invalidStudnr = 456789;
        studentRepository.updateStudent(new Student("PeetersAndereAchternaam", "PeetersAndereVoornaam", invalidStudnr, true));
    }

    @Test
    public void updateStudent_updatesAllProperties() {
        studentRepository.updateStudent(new Student("PeetersAndereAchternaam", "PeetersAndereVoornaam", 456, true));
        List<Student> students = studentRepository.getStudentsByName("PeetersAndereAchternaam");
        Assert.assertEquals("Expected changed student name 'PeetersAndereAchternaam' to be found in DB!", 1, students.size());

        var updatedFromDb = students.get(0);
        Assert.assertEquals("PeetersAndereAchternaam", updatedFromDb.getNaam());
        Assert.assertEquals("PeetersAndereVoornaam", updatedFromDb.getVoornaam());
        Assert.assertEquals(456, updatedFromDb.getStudnr());
        Assert.assertEquals(true, updatedFromDb.isGoedBezig());
    }

    @Test
    public void saveStudent_addsNewStudentInDb() {
        studentRepository.saveNewStudent(new Student("Johanna", "Sofie", 23356, true));

        List<Student> result = studentRepository.getStudentsByName("Johanna");
        Assert.assertEquals("Name should be Johanna", "Johanna", result.get(0).getNaam());
    }

}
