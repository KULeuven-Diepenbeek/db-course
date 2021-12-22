package be.kuleuven.studenthibernate;

import be.kuleuven.studenthibernate.domain.Student;
import be.kuleuven.studenthibernate.domain.StudentRepositoryJpaImpl;

import javax.persistence.Persistence;

public class Main {

    public static void main(String[] args) {
        System.out.println("Bootstrapping JPA/Hibernate...");
        var sessionFactory = Persistence.createEntityManagerFactory("be.kuleuven.studenthibernate.domain");
        var entityManager = sessionFactory.createEntityManager();

        System.out.println("Bootstrapping Repository...");
        var repo = new StudentRepositoryJpaImpl(entityManager);

        // TODO pruts hier om in 'productie' te testen - enkel NADAT testen in orde zijn!
        System.out.println("Persisting Jos Lowiemans, Truus Van Hooibergen...");
        var jos = new Student("Lowiemans", "Jos", true);
        var truus = new Student("Van Hooibergen", "Truus", true);

        repo.saveNewStudent(truus);
        repo.saveNewStudent(jos);

        isJosEr(repo);
    }

    private static void isJosEr(StudentRepositoryJpaImpl repo) {
        System.out.println("Is Jos er?");
        var studenten = repo.getStudentsByName("Lowiemans");
        for(var eenStudent : studenten) {
            System.out.println(eenStudent);
        }
    }
}
