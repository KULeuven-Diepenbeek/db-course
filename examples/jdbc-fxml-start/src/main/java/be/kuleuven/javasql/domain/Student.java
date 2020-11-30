package be.kuleuven.javasql.domain;

public class Student {
    private String naam;
    private String voornaam;
    private int studnr;
    private boolean goedBezig;

    public Student() {

    }

    @Override
    public String toString() {
        return "Student{" +
                "maam='" + naam + '\'' +
                ", voornaam='" + voornaam + '\'' +
                ", studnr=" + studnr +
                ", goedBezig=" + goedBezig +
                '}';
    }

    public Student(String naam, String voornaam, int studnr, boolean goedBezig) {
        this.naam = naam;
        this.voornaam = voornaam;
        this.studnr = studnr;
        this.goedBezig = goedBezig;
    }

    public int getStudnr() {
        return studnr;
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

    public void setGoedBezig(boolean goedBezig) {
        this.goedBezig = goedBezig;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public void setStudnr(int studnr) {
        this.studnr = studnr;
    }

    public void setVoornaam(String voornaam) {
        this.voornaam = voornaam;
    }
}
