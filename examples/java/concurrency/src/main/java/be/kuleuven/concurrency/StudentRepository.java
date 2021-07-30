package be.kuleuven.concurrency;

import java.util.List;

public interface StudentRepository {

    List<Student> getAllStudents();
    void saveNewStudent(Student student);
    void updateStudent(Student student);
    void deleteStudent(Student student);

}
