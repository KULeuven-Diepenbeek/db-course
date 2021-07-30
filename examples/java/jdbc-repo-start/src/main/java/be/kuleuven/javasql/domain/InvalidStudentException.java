package be.kuleuven.javasql.domain;

public class InvalidStudentException extends RuntimeException {

    public InvalidStudentException(Student student) {
        super("Student " + student.getStudnr() + " not found in DB");
    }

}
