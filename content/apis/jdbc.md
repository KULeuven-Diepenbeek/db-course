---
title: JDBC - Java Database Connectivity
draft: false
---

## Java Database Connectivity (JDBC)

### Hoe verbind ik Java met de DB?

[JDBC](https://www.tutorialspoint.com/jdbc/index.htm) is een interface in de JDK die ons in staat stelt om een connectie te openen naar een database. **JDBC is een API**: een abstracitelaag of een _protocol_. Dit betekent dat we met JDBC kunnen verbinden naar eender welke server van eender welke flavor: een Oracle SQL, MSSQL, of SQLite database. De database _vendor_ wordt verborgen achter de JDBC laag. Voor deze oefeningen beperken we ons tot MySQL.

Voor elke database moet er dus een vendor-specifieke driver als dependency worden toegevoegd. In het geval van MySQL is dit de [mysql-jdbc](https://mvnrepository.com/artifact/mysql/mysql-connector-java) driver, de `mysql-jdbc` package. JDBC zelf leeft in `java.sql` en is een integraal onderdeel van de JDK: dit moeten we dus niet apart oplijsten als dependency of downloaden.

{{<mermaid align="left">}}
graph LR;
    Java[Java]
    JDBC[JDBC]
    MYSQL[MySQL-JDBC]
    DB[(MySQL Database)]
    subgraph Java space
    subgraph JDK
    Java -.-> JDBC
    end
    JDBC --> MYSQL
    end
    subgraph DB space
    MYSQL --> DB
    end
{{< /mermaid >}}

De `mysql-jdbc` package zorgt voor de brug tussen onze Java applicatie en de database, maar we spreken die aan via JDBC. 

Enkele belangrijke statements:

1. Een connectie naar een database vastleggen: `var connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/<database_name>", "root", "");`
{{% notice warning %}}
Wil je meerdere queries tegelijk uitvoeren dan moet je dit ook nog specifiek vermelden in de driver door `?allowMultiQueries=true` toe te voegen aan de database url bv: `"jdbc:mysql://localhost:3306/<database_name>?allowMultiQueries=true"`
{{% /notice %}}
2. Een `SELECT` query uitvoeren: `var s = connection.createStatement(); var result = s.executeQuery("..."); var cell = result.getString("<column_name>");`
3. Een `INSERT`/`UPDATE`/... query uitvoeren (die de structuur of inhoud van de database **wijzigt**): `var s = connection.createStatement(); s.executeUpdate("...");`

Het volgende voorbeeld opent een verbinding naar een DB, maakt een tabel aan, voegt een record toe, en telt het aantal records:

```java
public static void createDb() throws SQLException {
    var connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/school", "root", "");
    var s = connection.createStatement();
    s.executeUpdate("DROP TABLE IF EXISTS `student`;");
    s.executeUpdate("CREATE TABLE student(nr INT);");
    s.executeUpdate("INSERT INTO student(nr) VALUES(1);");
    s.close();
    connection.close();
}

public static void verifyDbContents() throws SQLException {
    var connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/school", "root", "");
    var s = connection.createStatement();
    var result = s.executeQuery("SELECT COUNT(*) as cnt FROM student;");
    while (result.next()) {
        System.out.println("Assert that number of rows is 1: " + (result.getInt("cnt") == 1));
        assert result.getInt("cnt") == 1;
    }
    s.close();
    connection.close();
}
```

**Gradle** dependency: ``
```groovy
// https://mvnrepository.com/artifact/mysql/mysql-connector-java
implementation 'mysql:mysql-connector-java:5.1.6'
```

Merk op dat `SQLException` een **checked exception** is die je constant moet meespelen in de method signature of expliciet moet opvangen. Het probleem van een `try { } catch { } finally { }` block is dat in de finally je ook geen `close()` kan uitvoeren zonder opnieuw een `try` block te openen... Inception!

Het `connection.close()` statement moet er voor zorgen dat voor elke request de connection netjes wordt afgesloten. Een database heeft meestal een **connection pool** van x aantal beschikbare connections, bijvoorbeeld 5. Als een connection per request niet wordt gesloten, heeft de volgende bezoeker van onze website geen enkele kans om zijn search query te lanceren, omdat de database dan zegt dat alle connecties zijn opgebruikt!

Merk op dat de String `jdbc:mysql://localhost:3306/school` een **connectie met je MariaDB** aanmaakt en de meegegeven database, zodat je met PHPmyAdmin data kan inspecteren. Indien je een tabel aanmaakt de eerste keer, gaat dit de tweede keer crashen met _table already exists_. Houd hier dus rekening mee (e.v.t. met `IF NOT EXISTS`). Je kan ook een **in-memory database** aanmaken, die volledig in RAM leeft en bij elke opstart opnieuw wordt aangemaakt, met de String `jdbc:sqlite:memory`. (Hiervoor gebruiken we dan de [sqlite JDBC-connector](https://github.com/xerial/sqlite-jdbc), hier gaan we in deze cursus echter niet verder op in.)

Werk je met een andere database maar heb je geen idee hoe die speciale connection string te vormen? Geen probleem, daarvoor dient [https://www.connectionstrings.com/](https://www.connectionstrings.com/). Bijvoorbeeld, een connectie naar de Microsoft Azure cloud kan met de volgende syntax:

```
Server=tcp:myserver.database.windows.net,1433;Database=myDataBase;User ID=mylogin@myserver;Password=myPassword;Trusted_Connection=False;Encrypt=True;
```

Het is de connection string die bepaalt welke dependency binding gebruikt wordt! Dit noemen we _late binding_: er is **geen expliciete referentie** naar iets van MySQL in de Java code; we werken _enkel_ met JDBC zelf. Als je de vendor driver vergeet toe te voegen als Gradle dependency gebeurt er dit:

```
Exception in thread "main" java.sql.SQLException: No suitable driver found for jdbc:mysql://localhost:3306/school
    at java.sql/java.mysql.DriverManager.getConnection(DriverManager.java:702)
    at java.sql/java.mysql.DriverManager.getConnection(DriverManager.java:251)
    at Demo.main(Demo.java:8)
```

{{% notice note %}}
In-memory databases (ConStr. `jdbc:sqlite:memory`), die met een lege database vertrekken, en constant `CREATE TABLE()` statements issuen, vervuilen je broncode. Als je veel SQL moet uitvoeren is het beter om dit in een `.sql` bestand te bewaren in `src/main/resources` en eenmalig in te lezen als SQL met `new String(Files.readAllBytes(Paths.g));`, om te kunnen uitvoeren via `statement.executeUpdate()`.
{{% /notice %}}

Bijvoorbeeld voor onze casus:
```java
private void initTables() throws Exception {
    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tennisvlaanderen?allowMultiQueries=true", "root", "");
    URI create_tables_path = Objects.requireNonNull(App.class.getClassLoader().getResource("create_tables.sql")).toURI();
    var create_tables_sql = new String(Files.readAllBytes(Paths.get(create_tables_path)));
    URI populate_tables_path = Objects.requireNonNull(App.class.getClassLoader().getResource("populate_tables_with_testdata.sql")).toURI();
    var populate_tables_sql = new String(Files.readAllBytes(Paths.get(populate_tables_path)));
    System.out.println(create_tables_sql);
    System.out.println(populate_tables_sql);

    var s = connection.createStatement();
    s.executeUpdate(create_tables_sql);
    s.executeUpdate(populate_tables_sql);
    s.close();
    connection.close();
}
```

### Queries/Objecten in JDBC


Stel dat we het eerste voorbeeld van een school database willen uitbreiden en studenten die in de database opgeslagen zijn willen inladen in een `Student` klasse instantie: van de `TABLE STUDENT` naar de `class Student`. In geval van JDBC is dat veel handwerk: 

1. Maak een verbinding met de database. 
2. Voer de `SELECT` statements uit. 
3. Loop door de `ResultSet` en maak een nieuwe `Student` instantie aan. Vang alle mogelijke fouten zelf op: wat met lege kolommen, `null`? Wat met `INTEGER` kolommen die je wilt mappen op een `String` property? 

Om van de huidige resultatenrij naar de volgende te springen in `ResultSet` gebruikt men de methode `next()` in een typisch `while()` formaat:

```java
var result = statement.executeQuery("SELECT * FROM <table_name>");
while(result.next()) {
    var eenString = result.getString("<column_name>");
    // doe iets!
}
```

Zie ook [ResultSet Oracle Javadoc](https://docs.oracle.com/javase/7/docs/api/java/sql/ResultSet.html). 

Aangezien we reeds hebben kennis gemaakt met de (beperkte) API, schakelen we onmiddellijk over naar de oefeningen:

### Demos

We gebruiken de `student` tabel statements uit _RDBMS Transacties - Failures & Rollbacks_ maar nu met MySQL syntax:
```sql
-- Drop tables if they exist
DROP TABLE IF EXISTS student;

-- Create student table
CREATE TABLE student (
    studnr INT NOT NULL PRIMARY KEY,
    naam VARCHAR(200) NOT NULL,
    voornaam VARCHAR(200),
    goedbezig BOOL
);

-- Insert sample data into student
INSERT INTO student (studnr, naam, voornaam, goedbezig) VALUES
(123, 'Trekhaak', 'Jaak', 0),
(456, 'Peeters', 'Jos', 0),
(890, 'Dongmans', 'Ding', 1);
```

We maken hier weer gebruik van Gradle, maar aangezien onze database op onze Windows host draait gaan we geen verbinding kunnen maken via onze WSL. Daarom moeten we eerst nog even Gradle voor windows installeren. De stappen daarvoor vind je [hier](https://docs.gradle.org/current/userguide/installation.html#ex-installing-manually). _(Je kan ook je bestanden aanmaken in je WSL en dan de projectmap kopiëren naar je windows file explorer. Dan kan je de Gradle wrapper voor Windows gebruiken `./gradle.bat`)_

#### Alles in de main
Om dingen te doen met de database moeten we dus een aantal stappen doorlopen, die hier beschreven en gecodeerd zijn in een `main`-method:
```java
public static void main(String[] args){
    try{
        // CONNECT TO MYSQL
        var connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/school", "root", "");

        // CREATE THE TABLES
        var statement = connection.createStatement();
        statement.executeUpdate("DROP TABLE IF EXISTS `student`;");
        statement.executeUpdate("""
                CREATE TABLE student(
                    studnr INT NOT NULL PRIMARY KEY,
                    naam TEXT NOT NULL,
                    voornaam TEXT,
                    goedbezig BOOLEAN
                );
                """);
        statement.executeUpdate("DROP TABLE IF EXISTS log;");
        statement.executeUpdate("""
                CREATE TABLE log(
                    id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
                    date DATETIME DEFAULT CURRENT_TIMESTAMP,
                    foreign_id INT NOT NULL,
                    msg TEXT
                );
                """);
        statement.executeUpdate(
                "INSERT INTO student(studnr, naam, voornaam, goedbezig) VALUES (123, 'Trekhaak', 'Jaak', 0);");
        statement.executeUpdate(
                "INSERT INTO student(studnr, naam, voornaam, goedbezig) VALUES (456, 'Peeters', 'Jos', 0);");
        statement.executeUpdate(
                "INSERT INTO student(studnr, naam, voornaam, goedbezig) VALUES (890, 'Dongmans', 'Ding', 1);");
        statement.close();

        //VERIFY DATABASE CONTENT
        statement = connection.createStatement();
        var result = statement.executeQuery("SELECT COUNT(*) as cnt FROM student;");
        while (result.next()){
            System.out.println("Assert that number of rows is 3: "+  (result.getInt("cnt") == 3));
            assert result.getInt("cnt") == 3;
        }

        //READ FROM DB
        statement = connection.createStatement();
        result = statement.executeQuery("SELECT * FROM student;");
        while (result.next()){
            System.out.println("Studnr: "+result.getInt("studnr"));
        }

        //UPDATE DB
        statement = connection.createStatement();
        statement.executeUpdate("UPDATE student SET voornaam = 'Jaqueline' WHERE studnr = 123;");
        
        // OPTIONAL VERIFY UPDATE WITH A READ

        // Closing all connections correctly
        result.close();
        statement.close();
        connection.close();

    }catch (Exception e){
        e.printStackTrace();
    }
}
```

#### Opsplitsen in verschillende methoden
Je merkt onmiddellijk dat deze code onoverzichtelijk is, je kan dus beter verschillende methoden aanmaken en dan oproepen in de `main`-method. We splitsen op in:
- `public static void connectToDbMysql(String connectionString, String user, String pwd)`
- `public static void createDbMysql()`
- `public static void verifyDbContents()`
- `public static ResultSet readFromDb(String query)`
- `public static void updateDb(String updateStr)`
- `public static void closeAllConnections()`

_Dit laten we als een oefening voor de student._

#### Opsplitsen van verantwoordelijkheden in verschillende klasses
Wanneer we nu met meerdere databases en meerdere tabellen werken gaat het niet meer overzichtelijk zijn om alles in dezelfde klasse te doen, daarom gaan we verantwoordelijkheden opsplitsen in verschillende klassen:
- `ConnectionManager` klasse die instaat voor de verbinding met de database.
- `<naam>Repository` klasse die instaat voor de logica die te maken heeft met queries uitvoeren die over een bepaalde model klasse gaan bv. We hebben een klasse `Student` en een tabel `student` dan komt in de StudentRepository alle code om onder andere data uit de studententabel op te halen en om te vormen tot echte student objecten in Java.
- In de main komt dan alles samen, data ophalen uit database en omvormen tot Java objecten en dan die objecten gebruiken.

We maken en testen een klasse `StudentRepository` die de volgende methode implementeert. Zoals je ziet is het de bedoeling dat de JDBC `Connection` instance elders wordt aangemaakt, bijvoorbeeld in een **aparte** `ConnectionManager` klasse. 

```java
public class StudentRepository {
        public StudentRepository(Connection connection);
        public List<Student> getStudentsByName(String name);
}
```
<details closed>
<summary><i><b>Klik hier voor de volledige implementatie van de <code>StudentRepository</code> klasse </b></i>🔽</summary>
<p>

```java
package be.kuleuven.student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentRepository {
    private final Connection connection;

    public StudentRepository(Connection connection) {
        this.connection = connection;
    }

    public List<Student> getStudentsByName(String name){
        ArrayList<Student> resultList = new ArrayList<Student>();
        try {
            Statement s = connection.createStatement();
            String stmt = "SELECT * FROM student WHERE naam = '" + name + "'";
            ResultSet result = s.executeQuery(stmt);

            while(result.next()) {
                int studnr = result.getInt("studnr");
                String naam  = result.getString("naam");
                String voornaam = result.getString("voornaam");
                boolean goedbezig = result.getBoolean("goedbezig");

                resultList.add(new Student(studnr, naam, voornaam, goedbezig));
            }
            s.close();

        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        return resultList;
    };
}
```

</p>
</details>

<details closed>
<summary><i><b>Klik hier voor de volledige implementatie van de <code>Student</code> klasse </b></i>🔽</summary>
<p>

```java
package be.kuleuven.student;

import java.util.Objects;

public class Student {
    private int studnr;
    private String naam,voornaam;
    private boolean goedBezig;

    public Student(){

    }
    public Student(int studnr, String naam, String voornaam, boolean goedBezig) {
        this.studnr = studnr;
        this.naam = naam;
        this.voornaam = voornaam;
        this.goedBezig = goedBezig;
    }

    public int getStudnr() {
        return studnr;
    }

    public void setStudnr(int studnr) {
        this.studnr = studnr;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getVoornaam() {
        return voornaam;
    }

    public void setVoornaam(String voornaam) {
        this.voornaam = voornaam;
    }

    public boolean isGoedBezig() {
        return goedBezig;
    }

    public void setGoedBezig(boolean goedBezig) {
        this.goedBezig = goedBezig;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studnr=" + studnr +
                ", naam='" + naam + '\'' +
                ", voornaam='" + voornaam + '\'' +
                ", goedBezig=" + goedBezig +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return studnr == student.studnr && Objects.equals(naam, student.naam) && Objects.equals(voornaam, student.voornaam) && Objects.equals(goedBezig, student.goedBezig);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studnr, naam, voornaam, goedBezig);
    }
}
```

</p>
</details>

<details closed>
<summary><i><b>Klik hier voor de volledige implementatie van de <code>ConnectionManager</code> klasse </b></i>🔽</summary>
<p>

```java
package be.kuleuven;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public class ConnectionManager {
    private Connection connection;
    private String connectionString;

    public ConnectionManager(String connectionString) {
        this.connectionString = connectionString;
        try {
            connection = DriverManager.getConnection(connectionString);
            connection.setAutoCommit(false);

            initTables();
            verifyTableContents();
        } catch (Exception e) {
            System.out.println("Db connection handle failure");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void flushConnection() {
        try {
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void initTables() throws Exception {
        String sql = new String(Files.readAllBytes(Paths.get("src/main/resources/dbcreate.sql")));
        System.out.println(sql);
        Statement s = connection.createStatement();
        s.executeUpdate(sql);
        s.close();
    }

    public void verifyTableContents() throws SQLException {
        Statement s = connection.createStatement();
        ResultSet result = s.executeQuery("SELECT COUNT(*) as cnt FROM student");
        assert result.getInt("cnt") == 3;
    }
}
```
</p>
</details>

<details closed>
<summary><i><b>Klik hier voor een voorbeeld van de <code>Main</code> klasse waar we bovenstaande klassen gebruiken </b></i>🔽</summary>
<p>

```java
package be.kuleuven;

import be.kuleuven.student.Student;
import be.kuleuven.student.StudentRepository;

import java.sql.*;
import java.util.List;

public class Main {
    private static Statement statement = null;
    private static ResultSet result = null;

    public static void main(String[] args) throws SQLException {
        ConnectionManager cm = new ConnectionManager("jdbc:mysql://localhost:3306/school", "root", "");
        cm.flushConnection();
        Connection connection = cm.getConnection();
        StudentRepository studentRepository = new StudentRepository(cm.getConnectionString());
        List<Student> result = studentRepository.getStudentsByName("Jaak");
        for (Student s: result) {
            System.out.println(s);
        }
    }
}
```

</p>
</details>

**Enkele vragen/oefeningen:**
1. Breid de `StudentRepository`-klasse uit met de volgende methoden (CREATE, READ, UPDATE, DELETE = **CRUD**):
```java
public class StudentRepository {
        public StudentRepository(Connection connection);
        public List<Student> getStudentsByName(String name);
        public List<Student> getStudentsByStudnr(int nr)
        public void saveNewStudent(Student student);
        public void updateStudent(Student student);
        public void deleteStudentByStudnr(int nr);
}
```
2. Hoe zou je bovenstaande `StudentRepository` unit (integratie) testen, zonder de "productie database" op te vullen met testdata? (Hint: kijk naar het constructor argument). Hoe kan je `getStudentsByName()` testen zonder de volgende oefening afgewerkt te hebben, die nieuwe studenten bewaren pas mogelijk maakt?
3. Breid dit uit met `saveNewStudent(Student)`.
4. Breid dit uit met `updateStudent(Student)`. Wat moet je doen als deze student nog niet in de database zit? Welke gegevens update je wel en welke niet? 
5. Merk op dat elke keer als je je project opstart je geen `CREATE TABLE student` kan uitvoeren als je een file-based SQLite bestand hanteert: eens de tabel is aangemaakt geeft een nieuwe create foutmeldingen. `DROP TABLE IF EXISTS student;` lost dit op, maar daardoor ben je ook altijd je data kwijt. Hoe los je dit probleem op?
6. Stel dat een `Student` is ingeschreven in een `Vak` met properties `naam` (vb. "databases") en `ects` (vb. 4). 
    - Maak een `VakRepository` om nieuwe vakken te bewaren.
    - Hoe link je de `Student` klasse met de `Vak` klasse? wat verandert er in de query van `getStudentsByName()`?

**BELANGRIJK**: 

- `executeUpdate()` van een `Statement` is erg omslachtig als je een string moet samenstellen die een `INSERT` query voorstelt (haakjes, enkele quotes, ...). Wat meer is, als de input van een UI komt, kan dit gehacked worden, door zelf de quote te sluiten in de string. Dit noemt men **SQL Injection**, en om dat te vermijden gebruik je in JDBC de `prepareStatement()` methode. Zie [JDBC Basics: Prepared Statements](https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html). De String die je meegeeft bevat in de plaats van parameters een vraagteken: `INSERT INTO STUDENT(bla, bla) VALUES(?, ?)`. Die parameters vul je daarna aan met `preparedStatement.setString()` of `setInt()`. Op die manier is de code zowel _netjes_ als _injectie-vrij_!
- Als je data wenst op te halen dat is verspreid over verschillende tabellen, is de kans groot dat een `JOIN` SQL statement nodig is. Probeer eerst de query te schrijven in de _PhpMyAdmin_ tool. De Java objecten opvullen is de laatste taak.

Bijvoorbeeld:
```java
...
try {
    String stmt = "INSERT INTO student(naam, voornaam, studnr, goedbezig) VALUES (?, ?, ?, ?)";
    System.out.println(stmt);
    PreparedStatement prepared = connection.prepareStatement(stmt);
    prepared.setString(1, "Verboven");
    prepared.setString(2, "Mark");
    prepared.setInt(3, 666);
    prepared.setBoolean(4, false);
    prepared.execute();
    prepared.close();
} catch(SQLException ex) {
    throw new RuntimeException(ex);
}
...
```
### Jdbc met SQLite

Een SQLite database kan handig zijn omdat een lokale `.db`-file al als database kan dienen wat de overhead van moeilijke connecties kan verminderen. Hiervoor moeten we dan een aantal dingen aanpassen: 
1. De driver dependency wordt nu: `implementation 'org.xerial:sqlite-jdbc:3.42.0.0'`
2. De Connection met een lokale database file kan je dan maken met: `var connection = (Connection) DriverManager.getConnection("jdbc:sqlite:mydatabase.db");`
    - Indien de databasefile nog niet bestaat wordt deze aangemaakt.
    - **Let op!** De import van connection mag nu niet de import zijn van de Mysql dependency maar wordt: `import java.sql.Connection;`
3. Let op dat je nu ook correcte SQLite syntax gebruikt in je queries, maar voor de rest zal alles gelijkaardig werken.

## EER-schema/database mapping naar Java Objects

Om dit te verduidelijken en in te oefenen gaan we de demo database wat uitbreiden zodat er ook one-to-many en many-to-many relationships in voorkomen. We voegen `opleiding`en toe en elke student volgt één opleiding en een opleiding heeft dus meerdere studenten. We voegen ook `vak`ken toe wat een many-to-many relatie oplevert aangezien een student meerdere vakken kan volgen en een vak meerdere studenten kan hebben.

<details closed>
<summary><i><b>Klik hier voor de sql code van deze uitgebreidere database</b></i>🔽</summary>
<p>

```sql
DROP TABLE IF EXISTS student_volgt_vak;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS vak;
DROP TABLE IF EXISTS opleiding;

CREATE TABLE opleiding(
    id INT NOT NULL PRIMARY KEY,
    opleidingsnaam VARCHAR(200) NOT NULL
);

CREATE TABLE student(
    studnr INT NOT NULL PRIMARY KEY,
    naam VARCHAR(200) NOT NULL,
    voornaam VARCHAR(200),
    goedbezig BOOLEAN,
    opleiding INT DEFAULT NULL,
    FOREIGN KEY (opleiding) REFERENCES opleiding(id)
);

CREATE TABLE vak(
    vaknr INT NOT NULL PRIMARY KEY,
    vaknaam VARCHAR(200) NOT NULL,
    opleiding INT DEFAULT NULL,
    FOREIGN KEY (opleiding) REFERENCES opleiding(id)
);


CREATE TABLE student_volgt_vak(
    id INT AUTO_INCREMENT PRIMARY KEY,
    student INT,
    vak INT,
    FOREIGN KEY (student) REFERENCES student(studnr),
	  FOREIGN KEY (vak) REFERENCES vak(vaknr)
);


INSERT INTO opleiding(id, opleidingsnaam) VALUES (1, 'IIW');

INSERT INTO student(studnr, naam, voornaam, goedbezig, opleiding) VALUES (123, 'Trekhaak', 'Jaak', 0, 1);
INSERT INTO student(studnr, naam, voornaam, goedbezig, opleiding) VALUES (456, 'Peeters', 'Jos', 0, 1);
INSERT INTO student(studnr, naam, voornaam, goedbezig, opleiding) VALUES (890, 'Dongmans', 'Ding', 1, NULL);

INSERT INTO vak(vaknr, vaknaam, opleiding) VALUES (1, 'DAB', 1);
INSERT INTO vak(vaknr, vaknaam, opleiding) VALUES (2, 'SES', 1);
INSERT INTO vak(vaknr, vaknaam, opleiding) VALUES (3, 'FSWEB', 1);

INSERT INTO student_volgt_vak(student, vak) VALUES (123, 1);
INSERT INTO student_volgt_vak(student, vak) VALUES (123, 2);
INSERT INTO student_volgt_vak(student, vak) VALUES (123, 3);
INSERT INTO student_volgt_vak(student, vak) VALUES (456, 1);
INSERT INTO student_volgt_vak(student, vak) VALUES (456, 2);
INSERT INTO student_volgt_vak(student, vak) VALUES (890, 1);
```

</p>
</details>

Hoe kan je die relaties nu zichtbaar maken in Java. Dit is vrij eenvoudig, een `Student` kan meerdere vakken volgen dus krijgt de student klasse een Lijst met vakken. Een student behoort ook tot een Opleiding, dus die opleiding wordt ook een datamember voor Student. Analoog zullen de klassen `Vak` en `Opleiding` een lijst van studenten hebben en zal een vak ook een opleiding als datamember hebben. 

Nu zal je zeker zeggen maar zijn we dan niet veel data aan het dupliceren en is dat niet waarom we een SQL database gebruiken. Dan ben je helemaal correct. Het verschil met de Java klassem/objecten is dat we enkel die objecten aanmaken die we op dat moment nodig hebben en die halen we uit ... je raad het al: de database. Dus het is volledig ok om zoveel data in een object op te slaan omdat we nooit alle data van de database in Java Objeten omvormen, we weten op voorhand bijvoorbeeld welke student we willen bekijken en kunnen dus specifiek voor die student een object aanmaken en de rest blijft gewoon in de database staan tot we het nodig hebben!

## Grotere oefening

Breid de demos van hierboven uit met alle data van de nieuwe databas:
1. Breid de `Student` klasse uit met een lijst van vakken en een opleiding.
```java
// Template van de klasse Student
public class Student {
        private int studnr;
        private String voornaam, achternaam;
        private boolean goedBezig;
        private Opleiding opleiding;
        private ArrayList<Vak> vakken;
        // Constructors
        // Getters en Setters
        // To String
        // Equals
        // Hash
}
```
2. Maak een klasse voor `Vak` en `Opleiding`.
```java
// Template van de klasse Vak
public class Student {
        private int vaknr;
        private String naam;
        private Opleiding opleiding;
        private ArrayList<Student> studenten;
        // Constructors
        // Getters en Setters
        // To String
        // Equals
        // Hash
}

// Template van de klasse Opleiding
public class Student {
        private String naam;
        private ArrayList<Student> inschrijvingen;
        // Constructors
        // Getters en Setters
        // To String
        // Equals
        // Hash
}
```
3. Voorzie nu voor de verschillende klassen corresponderende `Repository`-klassen waar alle logica in te staan komt om de data over de verschillende klassen uit de database te halen en om te vormen tot Java objecten. (Je moet dus je `StudentRepository`-klasse aanpassen, een `VakRepository`-klasse en een `OpleidingRepository`-klasse aanmaken)
    - Om de lijsten op te stellen kan je best van handige SQL-queries gebruik maken. Hier vind je enkele voorbeelden:
    ```sql
    -- Krijg alle studenten die behoren tot een opleiding (vervang ? door een opleiding id)
    SELECT s.* FROM student s WHERE s.opleiding = ?;
    -- Krijg alle vakken waarvoor een specifieke student is ingeschreven (vervang ? door een studnr)
    SELECT v.* FROM student_volgt_vak svv JOIN vak v ON svv.vak = v.vaknr WHERE svv.student = ?;
    -- Krijg alle studenten die behoren tot een vak (vervang ? door een vaknr)
    SELECT s.* FROM student_volgt_vak svv JOIN student s ON svv.student = s.studnr WHERE svv.vak = ?;
    ```
4. Maak van je `App.java` een soort command line administratie systeem waarmee je een aantal dingen kan doen (Zorg er natuurlijk voor dat alle wijzigingen ook doorgevoerd worden in de database):
    - voor een gegeven student opvragen voor welke vakken hij/zij is ingeschreven.
    - voor een gegeven student opvragen voor welke opleiding hij/zij is ingeschreven.
    - voor een gegeven vak opvragen welke studenten ingeschreven zijn.
    - voor een gegeven opleiding opvragen welke studenten ingeschreven zijn.
    - studenten uitschrijven voor een vak.
    - studenten inschrijven voor een vak.
    - studenten uitschrijven voor een opleiding.
    - studenten inschrijven voor een opleiding.
    - studenten, vakken en opleidingen aanmaken.