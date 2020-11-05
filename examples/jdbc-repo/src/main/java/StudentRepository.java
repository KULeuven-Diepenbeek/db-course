import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class StudentRepository {

    private final Connection connection;

    public StudentRepository(Connection connection) {
        this.connection = connection;
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

        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }

        return resultList;
    }

}
