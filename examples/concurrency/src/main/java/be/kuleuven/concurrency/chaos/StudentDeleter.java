package be.kuleuven.concurrency.chaos;

import be.kuleuven.concurrency.Student;
import be.kuleuven.concurrency.StudentRepository;

import java.util.Comparator;
import java.util.List;

public class StudentDeleter implements Runnable {

    private final int threadNr;
    private final StudentRepository repository;

    public StudentDeleter(StudentRepository repository, int threadNr) {
        this.threadNr = threadNr;
        this.repository = repository;
    }

    @Override
    public void run() {
        List<Student> students = repository.getAllStudents();
        students.sort(Comparator.comparingInt(Student::getStudnr));
        repository.deleteStudent(students.get(0));
    }
}
