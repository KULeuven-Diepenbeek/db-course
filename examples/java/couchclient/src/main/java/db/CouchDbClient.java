package db;

import domain.Student;

public class CouchDbClient {

    private final org.lightcouch.CouchDbClient client;

    public CouchDbClient() {
        /*
            this thing WILL throw a CouchDbException if you somehow misconfigured things in couchdb.properties
            for instance, in case of wrong credentials:
            INFO: < Status: 401
            Exception in thread "main" org.lightcouch.CouchDbException: Unauthorized{"error":"unauthorized","reason":"You are not a server admin."}
         */
        client = new org.lightcouch.CouchDbClient();

    }

    public void query() {
        var joske = new Student("Joske", 10);
        client.save(joske);

        client.shutdown();
    }
}
