package be.kuleuven.concurrency.chaos;

import be.kuleuven.concurrency.Student;
import be.kuleuven.concurrency.StudentRepository;

public class StudentAdder implements Runnable {

    private final int threadNr;
    private final StudentRepository repository;

    public StudentAdder(StudentRepository repository, int nr) {
        this.repository = repository;
        this.threadNr = nr;
    }

    @Override
    public void run() {
        this.repository.saveNewStudent(new Student("Chaos " + threadNr, "Jos " + threadNr, threadNr + 100, true));
    }
}
