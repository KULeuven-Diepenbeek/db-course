package be.kuleuven.studenthibernate.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Docent {

    @Column
    @Id
    @GeneratedValue
    private int docentnummer;

    @Column
    private String naam;

    @OneToMany(mappedBy = "docent")
    private List<Student> studenten;

    public Docent() {

    }

    public Docent(String naam) {
        studenten = new ArrayList<>();
        this.naam = naam;
    }

    public void geefLesAan(Student student) {
        studenten.add(student);
        student.setDocent(this);
    }

    public String getNaam() {
        return naam;
    }

    public int getDocentnummer() {
        return docentnummer;
    }

    public List<Student> getStudenten() {
        return studenten;
    }
}
