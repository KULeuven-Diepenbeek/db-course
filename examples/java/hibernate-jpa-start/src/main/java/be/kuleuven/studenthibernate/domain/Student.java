package be.kuleuven.studenthibernate.domain;

public class Student {

    private String naam;
    private String voornaam;
    private boolean goedBezig;
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
