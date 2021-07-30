package be.kuleuven.studenthibernate.domain

import javax.persistence.*

// TODO add the needed annotations (don't forget the no-arg plugin: https://kotlinlang.org/docs/no-arg-plugin.html)
data class Student(var naam: String = "", var voornaam: String = "", var goedBezig: Boolean = false, var studentenNummer: Int = 0)
