package be.kuleuven.javasql;

import be.kuleuven.javasql.domain.InvalidStudentException;
import be.kuleuven.javasql.domain.Student;
import be.kuleuven.javasql.domain.StudentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public abstract class BaseStudentRepositoryTest {

    protected StudentRepository studentRepository;

    @Test
    public void getStudentsByName_NameUnknown_ReturnsEmptyList() {
        List<Student> result = studentRepository.getStudentsByName("bloekiebla");
        Assertions.assertNotNull(result, "result should not be null");
        Assertions.assertTrue(result.size() == 0, "resultset should be zero");
    }

    @Test
    public void getStudentsByName_NameKnownInDb_ReturnsListWithRecord() {
        // We weten dat student "Peeters" reeds in de DB zit.

        List<Student> result = studentRepository.getStudentsByName("Peeters");
        Assertions.assertNotNull(result, "result should not be null");
        Assertions.assertTrue(result.size() == 1, "resultset should be one record");
        Assertions.assertEquals(result.get(0).getNaam(), "Peeters", "Name should be Peeters");
    }

    @Test
    public void saveStudent_samePrimaryKey_ShouldCrash() {
        // We weten dat student "Peeters" reeds in de DB zit met als key 456.
        Assertions.assertThrows(RuntimeException.class, () -> {
            studentRepository.saveNewStudent(new Student("PeetersCopy", "Jozefien", 456, true));
        });
    }

    @Test
    public void updateStudent_unknownStudnr_throwsInvalidStudentException() {
        Assertions.assertThrows(InvalidStudentException.class, () -> {
            var invalidStudnr = 456789;
            studentRepository.updateStudent(new Student("PeetersAndereAchternaam", "PeetersAndereVoornaam", invalidStudnr, true));
        });
    }

    @Test
    public void updateStudent_updatesAllProperties() {
        studentRepository.updateStudent(new Student("PeetersAndereAchternaam", "PeetersAndereVoornaam", 456, true));
        List<Student> students = studentRepository.getStudentsByName("PeetersAndereAchternaam");
        Assertions.assertEquals(1, students.size(), "Expected changed student name 'PeetersAndereAchternaam' to be found in DB!");

        var updatedFromDb = students.get(0);
        Assertions.assertEquals(updatedFromDb.getNaam(), "PeetersAndereAchternaam");
        Assertions.assertEquals(updatedFromDb.getVoornaam(), "PeetersAndereVoornaam");
        Assertions.assertEquals(updatedFromDb.getStudnr(), 456);
        Assertions.assertEquals(updatedFromDb.isGoedBezig(), true);
    }

    @Test
    public void saveStudent_addsNewStudentInDb() {
        studentRepository.saveNewStudent(new Student("Johanna", "Sofie", 23356, true));

        List<Student> result = studentRepository.getStudentsByName("Johanna");
        Assertions.assertEquals("Johanna", result.get(0).getNaam(), "Name should be Johanna");
    }

}
