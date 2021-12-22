package be.kuleuven.studenthibernate.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Student {

    @Column
    private String naam;
    @Column
    private String voornaam;
    @Column
    private boolean goedBezig;

    @Column
    @Id
    @GeneratedValue
    private int studentenNummer;

    public Student() {

    }
    public int getStudentenNummer() {
        return studentenNummer;
    }

    public String getNaam() {
        return naam;
    }

    public String getVoornaam() {
        return voornaam;
    }

    public boolean isGoedBezig() {
        return goedBezig;
    }

    public void setVoornaam(String voornaam) {
        this.voornaam = voornaam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public void setGoedBezig(boolean goedBezig) {
        this.goedBezig = goedBezig;
    }

    @Override
    public String toString() {
        return "Student{" +
                "naam='" + naam + '\'' +
                ", voornaam='" + voornaam + '\'' +
                ", goedBezig=" + goedBezig +
                ", studentenNummer=" + studentenNummer +
                '}';
    }

    public Student(String naam, String voornaam, boolean goedBezig) {
        this.naam = naam;
        this.voornaam = voornaam;
        this.goedBezig = goedBezig;
    }
}
