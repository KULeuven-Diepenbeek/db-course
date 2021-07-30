import db.HashMapSerializer;

import java.io.*;

public class PlaygroundMain {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        System.out.println("starting query test...");
        //new db.CouchDbClient().query();

        System.out.println("saving joske...");
        HashMapSerializer.saveHashMap();
        System.out.println("re-loading joske from file...");
        HashMapSerializer.loadHashMap();


        System.out.println("done!");
    }


}
