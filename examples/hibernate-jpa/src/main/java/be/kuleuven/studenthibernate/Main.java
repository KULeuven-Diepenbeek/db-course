package be.kuleuven.studenthibernate;

import be.kuleuven.studenthibernate.domain.Docent;
import be.kuleuven.studenthibernate.domain.Student;
import be.kuleuven.studenthibernate.domain.StudentRepositoryJpaImpl;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

public class Main {

    public static void main(String[] args) {
        System.out.println("Bootstrapping JPA/Hibernate...");
        var sessionFactory = Persistence.createEntityManagerFactory("be.kuleuven.studenthibernate.domain");
        var entityManager = sessionFactory.createEntityManager();

        System.out.println("Bootstrapping Repository...");
        var repo = new StudentRepositoryJpaImpl(entityManager);

        isJosEr(repo);

        System.out.println("Persisting Jos Lowiemans, Truus Van Hooibergen...");
        var jos = new Student("Lowiemans", "Jos", true);
        var truus = new Student("Van Hooibergen", "Truus", true);
        repo.saveNewStudent(truus);
        repo.saveNewStudent(jos);
        System.out.println("Truus heeft ID gekregen: " + truus.getStudentenNummer());
        System.out.println("Jos heeft ID gekregen: " + jos.getStudentenNummer());

        isJosEr(repo);

        docentenTest(entityManager, jos);
        entityManager.clear();
        entityManager.close();
        entityManager = sessionFactory.createEntityManager();

        var student = entityManager.find(Student.class, jos.getStudentenNummer());
        System.out.println("student " + student.getNaam());
        System.out.println(" -- heeft als docent " + student.getDocent().getNaam());
    }

    private static void docentenTest(EntityManager entityManager, Student student) {
        var wouter = new Docent("Wouter");
        wouter.geefLesAan(student);

        entityManager.getTransaction().begin();
        entityManager.persist(wouter);
        entityManager.getTransaction().commit();
    }

    private static void isJosEr(StudentRepositoryJpaImpl repo) {
        System.out.println("Is Jos er?");
        for(var eenStudent : repo.getStudentsByName("Lowiemans")) {
            System.out.println(eenStudent);
        }
    }
}
