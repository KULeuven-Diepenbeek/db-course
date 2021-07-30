package be.kuleuven.concurrency.chaos;

import be.kuleuven.concurrency.Student;
import be.kuleuven.concurrency.StudentRepository;

import java.util.Comparator;
import java.util.List;

public class StudentUpdater implements Runnable {

    private final int threadNr;
    private final StudentRepository repository;

    public StudentUpdater(StudentRepository repository, int threadNr) {
        this.threadNr = threadNr;
        this.repository = repository;
    }

    @Override
    public void run() {
        List<Student> students = repository.getAllStudents();
        students.sort(Comparator.comparingInt(Student::getStudnr));
        Student lastStudent = students.get(students.size() - 1);
        lastStudent.setMaam(lastStudent.getMaam() + "rev" + threadNr);

        repository.updateStudent(lastStudent);
    }
}
