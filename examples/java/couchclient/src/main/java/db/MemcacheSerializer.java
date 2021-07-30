package db;

import domain.Student;

public class MemcacheSerializer {

    public void serialize() {
        var joske = new Student("Joske", 12);
        var jozefien = new Student("Jozefien", 22);
    }
}
