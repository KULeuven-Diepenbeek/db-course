package be.kuleuven.studenthibernate.domain

import java.lang.UnsupportedOperationException
import javax.persistence.EntityManager

interface StudentRepository {
    fun getStudentsByName(name: String): List<Student>
    fun saveNewStudent(student: Student)
    fun updateStudent(student: Student)
}

class StudentRepositoryJpaImpl(val entityManager: EntityManager) : StudentRepository {
    override fun getStudentsByName(name: String): List<Student> {
        throw UnsupportedOperationException()
    }

    override fun saveNewStudent(student: Student) {
        throw UnsupportedOperationException()
    }

    override fun updateStudent(student: Student) {
        throw UnsupportedOperationException()
    }

}
