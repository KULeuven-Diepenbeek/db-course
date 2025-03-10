package be.kuleuven.studenthibernate

import be.kuleuven.studenthibernate.domain.Student
import be.kuleuven.studenthibernate.domain.StudentRepository
import be.kuleuven.studenthibernate.domain.StudentRepositoryJpaImpl
import javax.persistence.Persistence

fun main(args: Array<String>) {
    println("Bootstrapping JPA/Hibernate...")
    val sessionFactory = Persistence.createEntityManagerFactory("be.kuleuven.studenthibernate.domain")
    val entityManager = sessionFactory.createEntityManager()

    println("Bootstrapping repository...")
    val repo = StudentRepositoryJpaImpl(entityManager)

    // TODO pruts hier om 'in productie' te testen - enkel NADAT de testen in orde zijn!

    isJosEr(repo)

    println("Persisting Jos Lowiemans, Truus Van Hooibergen...")
    val jos = Student("Lowiemans", "Jos", goedBezig = true)
    val truus = Student("Van Hooibergen", "Truus", goedBezig = true)
    with(repo) {
        saveNewStudent(jos)
        saveNewStudent(truus)
    }
    println("Jos heeft ID gekregen: ${jos.studentenNummer}")
    println("Truus heeft ID gekregen: ${truus.studentenNummer}")

    isJosEr(repo)
}

fun isJosEr(repo: StudentRepository) {
    println("Is Jos er?")
    repo.getStudentsByName("Lowiemans").forEach { println(it) }
}