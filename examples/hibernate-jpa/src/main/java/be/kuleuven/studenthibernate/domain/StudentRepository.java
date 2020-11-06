package be.kuleuven.studenthibernate.domain;

import java.util.List;

public interface StudentRepository {

    List<Student> getStudentsByName(String student);
    void saveNewStudent(Student student);
    void updateStudent(Student student);

}