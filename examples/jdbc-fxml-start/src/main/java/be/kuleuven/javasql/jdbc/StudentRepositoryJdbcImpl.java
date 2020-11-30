package be.kuleuven.javasql.jdbc;

import be.kuleuven.javasql.domain.Student;
import be.kuleuven.javasql.domain.StudentRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentRepositoryJdbcImpl implements StudentRepository {

    private final Connection connection;

    public StudentRepositoryJdbcImpl(Connection connection) {
        this.connection = connection;
    }



    public void saveNewStudent_withPreparedStatement(Student student) {
        try {
            var prepared = connection.prepareStatement("INSERT INTO student(naam, voornaam, studnr, goedbezig) VALUES (?, ?, ?, ?)");
            prepared.setString(1, student.getNaam());
            prepared.setString(2, student.getVoornaam());
            prepared.setInt(3, student.getStudnr());
            prepared.setBoolean(4, student.isGoedBezig());
            prepared.execute();

            prepared.close();
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void saveNewStudent_withoutPreparedStatement(Student student) {
        try {
            var s = connection.createStatement();
            s.executeUpdate("INSERT INTO student(naam, voornaam, studnr, goedbezig) VALUES ('" + student.getNaam() + "', '" + student.getVoornaam() + "', " + student.getStudnr() + ", " +
                    student.isGoedBezig() + ")");
            s.close();
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<Student> getStudentsByName(String student) {
        var resultList = new ArrayList<Student>();
        try {
            var s = connection.createStatement();
            var result = s.executeQuery("SELECT * FROM student WHERE naam = '" + student + "'");

            while(result.next()) {
                var studnr = result.getInt("studnr");
                var naam  = result.getString("naam");
                var voornaam = result.getString("voornaam");
                var goedbezig = result.getBoolean("goedbezig");

                resultList.add(new Student(naam, voornaam, studnr, goedbezig));
            }
            s.close();

        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }

        return resultList;
    }

    @Override
    public void saveNewStudent(Student student) {
        saveNewStudent_withPreparedStatement(student);
    }

    @Override
    public void updateStudent(Student student) {
        // TODO nu is het aan u
    }

}
