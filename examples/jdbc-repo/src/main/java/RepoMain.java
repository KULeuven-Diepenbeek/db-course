public class RepoMain {

    public static void main(String[] args) {
        var manager = new ConnectionManager();
        var repo = new StudentRepository(manager.getConnection());

        System.out.println("We vragen studenten op die 'Peeters' heten...");
        for (var student : repo.getStudentsByName("Peeters")) {
            System.out.println(student);
        }
    }
}
