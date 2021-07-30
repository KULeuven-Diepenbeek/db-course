package be.kuleuven.concurrency;

public class Student {
    private String maam;
    private String voornaam;
    private int studnr;
    private boolean goedBezig;

    public Student() {

    }

    @Override
    public String toString() {
        return "Student{" +
                "maam='" + maam + '\'' +
                ", voornaam='" + voornaam + '\'' +
                ", studnr=" + studnr +
                ", goedBezig=" + goedBezig +
                '}';
    }

    public Student(String maam, String voornaam, int studnr, boolean goedBezig) {
        this.maam = maam;
        this.voornaam = voornaam;
        this.studnr = studnr;
        this.goedBezig = goedBezig;
    }

    public int getStudnr() {
        return studnr;
    }

    public String getMaam() {
        return maam;
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

    public void setMaam(String maam) {
        this.maam = maam;
    }

    public void setStudnr(int studnr) {
        this.studnr = studnr;
    }

    public void setVoornaam(String voornaam) {
        this.voornaam = voornaam;
    }
}
