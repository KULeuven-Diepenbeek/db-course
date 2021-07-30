package db;

import domain.Student;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HashMapSerializer {
    public static void loadHashMap() throws IOException, ClassNotFoundException {
        var file = new File("database.db");
        var stream = new FileInputStream(file);
        var s = new ObjectInputStream(stream);
        Map<String, Object> map = (Map<String, Object>) s.readObject();
        s.close();

        Student joske = (Student) map.get("joske");

        System.out.println(joske.getName());
    }

    public static void saveHashMap() throws IOException {
        var db = new HashMap<String, Object>();
        db.put("joske", new Student("Joske", 11));

        File file = new File("database.db");
        FileOutputStream f = new FileOutputStream(file);
        ObjectOutputStream s = new ObjectOutputStream(f);
        s.writeObject(db);
        s.close();
    }
}
