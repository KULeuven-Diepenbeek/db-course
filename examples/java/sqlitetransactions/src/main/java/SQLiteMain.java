import java.sql.SQLException;

public class SQLiteMain {

    public static void main(String[] args) {
        System.out.println("Will be doing stuff:");
        new SQLiteTransactionTest().doStuff();
        System.out.println("Done doing stuff");
    }
}
