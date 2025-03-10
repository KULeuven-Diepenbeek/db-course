package be.kuleuven.studenthibernate.domain

import javax.persistence.EntityManager
import javax.persistence.criteria.Path
import javax.persistence.criteria.Root
import kotlin.reflect.KProperty1

interface StudentRepository {
    fun getStudentsByName(name: String): List<Student>
    fun saveNewStudent(student: Student)
    fun updateStudent(student: Student)
}

// this reduces root.get<String>("naam") into root.get(Student::naam)
// See http://lifeinide.com/post/2021-04-29-making-jpa-criteria-api-less-awkward-with-kotlin/
fun <T, V> Root<T>.get(prop: KProperty1<T, V>): Path<V> = this.get(prop.name)

class StudentRepositoryJpaImpl(val entityManager: EntityManager) : StudentRepository {
    override fun getStudentsByName(name: String): List<Student> {
        val criteriaBuilder = entityManager.criteriaBuilder
        val query = criteriaBuilder.createQuery(Student::class.java)
        val root = query.from(Student::class.java)

        query.where(criteriaBuilder.equal(root.get(Student::naam), name))
        return entityManager.createQuery(query).resultList
    }

    override fun saveNewStudent(student: Student) {
        with(entityManager) {
            transaction.begin()
            persist(student)
            transaction.commit()
        }
    }

    override fun updateStudent(student: Student) {
        with(entityManager) {
            transaction.begin()
            merge(student)
            transaction.commit()
        }
    }

}