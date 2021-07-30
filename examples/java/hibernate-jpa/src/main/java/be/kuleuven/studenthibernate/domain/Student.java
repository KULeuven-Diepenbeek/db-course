package be.kuleuven.studenthibernate.domain;

import javax.persistence.*;

@Entity
public class Student {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "docent_nr")
    private Docent docent;

    @Column
    private String naam;
    @Column
    private String voornaam;
    @Column
    private boolean goedBezig;
    @Column(name = "studnr")
    @Id
    @GeneratedValue
    private int studentenNummer;

    @PrePersist
    private void prePersist() {
        System.out.println("Bezig met het bezigen van het opslaan van student " + this);
    }

    public Student() {

    }

    public void setDocent(Docent docent) {
        this.docent = docent;
    }

    public Docent getDocent() {
        return docent;
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
