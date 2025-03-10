package be.kuleuven.studenthibernate.domain

import javax.persistence.*

@Entity
data class Student(
    @Column var naam: String,
    @Column var voornaam: String,
    @Column var goedBezig: Boolean = false,
    @Column(name = "studnr") @Id @GeneratedValue var studentenNummer: Int = 0) {

    @PrePersist
    fun prePersist() {
        println("Bezig met het bezigen van het opslaan van student $this!")
    }

}