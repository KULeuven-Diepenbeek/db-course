package db;

import domain.Student;

public class CouchDbClient {

    private final org.lightcouch.CouchDbClient client;

    public CouchDbClient() {
        client = new org.lightcouch.CouchDbClient();
    }

    public void query() {
        var joske = new Student("Joske", 10);
        client.save(joske);

        client.shutdown();
    }
}
