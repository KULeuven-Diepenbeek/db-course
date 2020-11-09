package be.kuleuven.concurrency.log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    public static void Log(Object s) {
        var formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        var date = new Date();

        System.out.println(formatter.format(date) + " (" + Thread.currentThread().getName() + ") -- " + s);
    }
}
