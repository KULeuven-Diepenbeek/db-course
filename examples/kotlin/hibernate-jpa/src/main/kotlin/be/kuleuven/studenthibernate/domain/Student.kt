package be.kuleuven.studenthibernate.domain

import javax.persistence.*

// WHY is this an "open" class all of a sudden, and not just a data class?
// See https://medium.com/swlh/defining-jpa-hibernate-entities-in-kotlin-1ff8ee470805
// JPA and Hibernate aren't designed to work with immutable classes
// Otherwise, this error occurs:
// org.hibernate.HibernateException: Getter methods of lazy classes cannot be final: be.kuleuven.studenthibernate.domain.Student#getStudentenNummer
//	at org.hibernate.proxy.pojo.ProxyFactoryHelper.validateGetterSetterMethodProxyability(ProxyFactoryHelper.java:96)
//	at org.hibernate.tuple.entity.PojoEntityTuplizer.buildProxyFactory(PojoEntityTuplizer.java:97)
//	at org.hibernate.tuple.entity.AbstractEntityTuplizer.<init>(AbstractEntityTuplizer.java:160)
//	at org.hibernate.tuple.entity.PojoEntityTuplizer.<init>(PojoEntityTuplizer.java:53)
//	at java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
//	at java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:64)
//	at java.base/jdk.internal.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
@Entity
open class Student(naam: String = "", voornaam: String = "", goedBezig: Boolean = false) {
    @Column open var naam: String = naam
    @Column open var voornaam: String = voornaam
    @Column open var goedBezig: Boolean = goedBezig

    @Column(name = "studnr")
    @Id
    @GeneratedValue
    open var studentenNummer: Int = 0

    @PrePersist
    fun prePersist() {
        println("Bezig met het bezigen van het opslaan van student $this!")
    }

    override fun toString(): String {
        return "Student(naam='$naam', voornaam='$voornaam', goedBezig=$goedBezig, studentenNummer=$studentenNummer)"
    }
}