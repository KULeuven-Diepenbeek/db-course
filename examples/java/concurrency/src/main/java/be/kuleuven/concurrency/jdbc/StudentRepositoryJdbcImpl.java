package be.kuleuven.concurrency.jdbc;

import be.kuleuven.concurrency.Student;
import be.kuleuven.concurrency.StudentRepository;
import be.kuleuven.concurrency.log.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
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
            String sql = "INSERT INTO student(naam, voornaam, studnr, goedbezig) VALUES (?, ?, ?, ?)";
            var prepared = connection.prepareStatement(sql);
            prepared.setString(1, student.getMaam());
            prepared.setString(2, student.getVoornaam());
            prepared.setInt(3, student.getStudnr());
            prepared.setBoolean(4, student.isGoedBezig());
            Logger.Log(sql + " of student " + student);
            prepared.execute();

            prepared.close();
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<Student> getAllStudents() {
        var resultList = new ArrayList<Student>();
        try {
            var s = connection.createStatement();
            String sql = "SELECT * FROM student";
            Logger.Log(sql);
            var result = s.executeQuery(sql);

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
        try {
            String sql = "UPDATE student SET naam = ?, voornaam = ?, goedbezig = ? WHERE studnr = ?";
            var prepared = connection.prepareStatement(sql);
            prepared.setString(1, student.getMaam());
            prepared.setString(2, student.getVoornaam());
            prepared.setBoolean(3, student.isGoedBezig());
            prepared.setInt(4, student.getStudnr());
            Logger.Log(sql + " of student " + student);
            prepared.execute();

            prepared.close();
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void deleteStudent(Student student) {
        try {
            var s = connection.createStatement();
            String sql = "DELETE from student WHERE studnr = " + student.getStudnr();
            Logger.Log(sql);
            s.executeUpdate(sql);
            s.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
