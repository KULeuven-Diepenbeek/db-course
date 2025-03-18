---
title: JDBC en JDBI
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
    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tennisvlaanderen", "root", "");
    var create_tables_sql = new String(Files.readAllBytes(Paths.get("src/main/resources/create_tables.sql")));
    var populate_tables_sql = new String(Files.readAllBytes(Paths.get("src/main/resources/populate_tables_with_testdata.sql")));
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
6. Stel dat een `Student` is ingeschreven in een `Cursus` met properties `naam` (vb. "databases") en `ects` (vb. 4). 
    - Maak een `CursusRepository` om nieuwe cursussen te bewaren.
    - Hoe link je de `Student` klasse met de `Cursus` klasse? wat verandert er in de query van `getStudentsByName()`?

**BELANGRIJK**: 

- `executeUpdate()` van een `Statement` is erg omslachtig als je een string moet samenstellen die een `INSERT` query voorstelt (haakjes, enkele quotes, ...). Wat meer is, als de input van een UI komt, kan dit gehacked worden, door zelf de quote te sluiten in de string. Dit noemt men **SQL Injection**, en om dat te vermijden gebruik je in JDBC de `prepareStatement()` methode. Zie [JDBC Basics: Prepared Statements](https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html). De String die je meegeeft bevat in de plaats van parameters een vraagteken: `INSERT INTO STUDENT(bla, bla) VALUES(?, ?)`. Die parameters vul je daarna aan met `preparedStatement.setString()` of `setInt()`. Op die manier is de code zowel _netjes_ als _injectie-vrij_!
- Als je data wenst op te halen dat is verspreid over verschillende tabellen, is de kans groot dat een `JOIN` SQL statement nodig is. Probeer eerst de query te schrijven in de _SQLite DB Browser_ tool. De Java objecten opvullen is de laatste taak.

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


<!-- ## Queries/Objecten in Jdbi 3

[Jdbi](https://jdbi.org) (Java DataBase Interface v3) is een lightweight library geschreven bovenop JDBC. Het gebruikt dus de interne Java API om te communiceren tussen de database en de Java applicatie. Echter, het maakt het leven voor ons als ontwikkelaar op heel wat vlakken véél _aangenamer_: waar JDBC eerder database-driven en dialect-afhankelijk is, is Jdbi eerder user-driven en met behulp van plugins dialect-onafhankelijk. 

JDBI3 is opgedeeld in modules, waarvan wij de volgende drie gaan gebruiken:

- `jdbi3-core` (altijd nodig) - voor JDBC zit dit in de JDK. 
- `jdbi3-sqlite` (voor de SQLite verbinding) - of andere DB driver
- `jdb3-sqlobject` - voor de eenvoudige mapping naar Plain Old Java Objects (POJOs)

```groovy
implementation group: 'org.jdbi', name: 'jdbi3-core', version: jdbiv
implementation group: 'org.jdbi', name: 'jdbi3-mysql', version: jdbiv
implementation group: 'org.jdbi', name: 'jdbi3-sqlobject', version: jdbiv
```

Met JDBI3 wordt op de volgende manier Java met de DB verbonden: -->

<div hidden>
{{<mermaid align="left">}}
graph LR;
    Java[Java]
    Jdbi[Jdbi3-core]
    JDBC[JDBC]
    JMYSQL[Jdbi3-MySQL]
    MYSQL[MySQL-JDBC]
    DB[(MyuSQL Database)]
    subgraph Java space
    Java --> Jdbi
    Jdbi --> JDBC
    Jdbi --> JMYSQL
    JMYSQL -.-> MYSQL
    JDBC -.-> MYSQL
    end
    subgraph DB space
    MYSQL --> DB
    end
{{< /mermaid >}}
</div>

<!--
Er komt dus één blokje bij tussen Java en JDBC: we gebruiken niet langer de ingebouwde JDK interfaces maar rechtstreeks de `jdbi-core` dependency die via JDBC de MySQL connectie maakt. De `jdbi3-mysql` package is afhankelijk van `mysql-jdbc`: zie [artifact dependency info](https://mvnrepository.com/artifact/org.jdbi/jdbi3-mysql). Met andere worden, het wordt een _transitieve_ dependency: deze verdwijnt uit onze `build.gradle`, maar wordt nog steeds meegetrokken met de rest.

Er is ook support voor spring, jpa, guava, kotlin, ...

Om bovenstaande JDBC oefening te implementeren in Jdbi3 hebben we eerst een extractie van een interface nodig voor de repository acties:

```java
public interface StudentRepository {
    List<Student> getStudentsByName(String student);
    void saveNewStudent(Student student);
    void updateStudent(Student student);
}
```

Nu kan `StudentRepositoryJdbcImpl` (hernoem bovenstaande) en onze nieuwe `StudentRepositoryJdbi3Impl` de interface `implements`-en. Denk aan de **Strategy design pattern** van SES: afhankelijk van een instelling kunnen we switchen van SQL leverancier, zolang de code overal de interface gebruikt. 
-->

<div hidden>
{{<mermaid align="left">}}
graph LR;
    Main[Controlller]
    Interface{StudentRepository}
    Jdbc[StudentRepositoryJdbcImpl]
    Jdbi[StudentRepositoryJdbi3Impl]
    Main --> Interface
    Interface --> Jdbc
    Interface --> Jdbi
{{< /mermaid >}}
</div>

<!--
### 1.2.1 JDBC vs Jdbi3

Geen idee waar te beginnen? Hier: http://jdbi.org/ 

#### **1. Connection openen**

In plaats van JDBC's `DriverManager.getConnection()` om de `Connection` instance te bootstrappen, gebruiken wij gewoon `Jdbi.create()` met ook één parameter, namelijk dezelfde ConnectionString. 

#### **2. Query uitvoeren**

In plaats van de vervelende checked `SQLException`s en de `createStatement()` code, heb je nu de keuze om ofwel de Fluent API te gebruiken:

```java
return jdbi.withHandle(handle -> {
   return handle.createQuery("SELECT * FROM student WHERE naam = :naam")
           .bind("naam", student)
           .mapToBean(Student.class)
           .list();
});
```

ofwel de Declarative API, waarbij je met de `@SqlQuery` kan werken op een interface:

```java
public interface StudentDao {
    @SqlQuery("SELECT * FROM student")
    @RegisterBeanMapper(Student.class)
    List<Student> getStudenten();
}
```

Dit vereist dat je de plugin `SqlObjectPlugin` installeert na de `Jdbi.create()`: `jdbi.installPlugin(new SqlObjectPlugin());`. Zie [jdbi.org](https://jdbi.org) documentatie.

{{% notice note %}}
Jdbi ondersteunt Kotlin met twee modules: `jdbi3-kotlin` en `jdbi3-kotlin-sqlobject` om data classes direct te binden aan een bepaalde tabel. Bovenstaande Java code (met `.bind()` werken) is analoog. Om verwarring te voorkomen zijn de Jdbi voorbeelden uitgewerkt in Java. Lees meer op http://jdbi.org/#_kotlin
{{% /notice %}}


Herinner je je nog de **SESsy Library**? Die werkte ook op die manier! Kijk nog eens in https://github.com/kuleuven-diepenbeek/sessylibrary in de map `src.main.java.be.kuleuven.sessylibrary.domain` in klasse `BooksRepository`!

Merk op dat Jdbi3 er voor kan zorgen dat de resultaten van je query automatisch worden vertaald naar een `Student` instantie door middel van **bean mapping**: de `mapToBean()` methode of de `@RegisterBeanMapper` annotatie. Die gaat via reflectie alle kolomnamen 1-op-1 mappen op properties van je object dat je wenst te mappen. Er zijn ook nog andere mogelijkheden, zoals mappen op een `HashMap`, ea:

![](/img/jdbi-map.jpg)

#### **3. Transacties, insert/update queries, ...**

Zelfstudie. Zie [jdbi.org](https://jdbi.org) documentatie.

### 1.2.2 Oefeningen

**Quickstart project**: `examples/jdbc-repo-start` in in de [cursus repository](https://github.com/kuleuven-Diepenbeek/db-course) ([download repo zip](https://github.com/KULeuven-Diepenbeek/db-course/archive/refs/heads/main.zip)). Deze bevat reeds bovenstaande JDBC implementatie en een aantal unit testen, waarvan er nog twee falen.

1. Fix eerst de falende unit testen!
2. Herimplementeer alle methodes van de `StudentRepository` interface hierboven, maar dan in Jdbi3 met de Fluent API (`jdbi.withHandle()`). Maak een tweede klasse genaamd `StudentRepositoryJdbi3`. Schrijf ook een bijhorende unit test klasse (kijk voor inspiratie naar de JDBC implementatie). Om te testen of het werkt in "productie" kan je je testcode van JDBC herbruiken door de code de **interface** te laten gebruiken in plaats van de implementatie. Bijvoorbeeld:

<div class="devselect">

```kt
fun main(args: Array<String>) {
    val jdbcRepo = StudentRepositoryJdbc(...)
    val jdbiRepo = StudentRepositoryJdbi3(...)
    doStuff(jdbcRepo)
}
fun doStuff(repository: StudentRepository) {
    // argunent = interface!
    // uw repository.getStudentsByName, saveNewStudent, ... tests hier
}
```

```java
public class OefeningMain {
        public static void main(String[] args) {
            var jdbcRepo = new StudentRepositoryJdbc(...);
            var jdbiRepo = new StudentRepositoryJdbi3(...);
            doStuff(jdbcRepo);
        }
        public static void doStuff(StudentRepository repository) {
            // argunent = interface!
            // uw repository.getStudentsByName, saveNewStudent, ... tests hier
        }
}
```
</div>

3. Implementeer opnieuw de `Cursus` link met de `Student`. Is het schrijven van `JOIN` queries in Jdbi3 eenvoudiger?
4. _Extra Oefening_: Maak een nieuwe implementatie van de repository interface die via de Jdbi3 Declaratie API de queries doorgeeft naar de SQLite DB. D.w.z., lees in de [Jdbi3 developer guide](http://jdbi.org/#_declarative_api) na hoe je de Declarative API gebruikt en verwerk dit. Tip: `jdbi.withExtension(StudentDao.class, ...)`. 

**Tip**:

- Neem de tijd om de JDBI documentatie uitvoerig te bekijken!

## 1.3 Jdbi Backend + JavaFX Frontend

Met Java database access enigszins onder de knie kijken we verder dan alleen maar de "repository". Op welke manier kunnen we onze `STUDENT` tabel visueel weergeven, en er studenten aan toevoegen of uit verwijderen? 

Dat kan op verschillende manieren, van HTML (SESsy Library) en JavaScript API calls naar iets eenvoudiger vanuit het eerstejaarsvak INF1: **JavaFX**. Je kan in JavaFX eenvoudig `TableView` stukken positioneren op een `AnchorPane` en die vullen met de juiste kolommen en rijen. De data blijft uiteraard uit de SQLite DB komen via JDBC/Jdbi. De `StudentRepository` is dus slechts één deel van het verhaal: waar wordt deze gebruikt? In JavaFX controllers. 

### 1.3.1 Een Gradle JavaFX Project

Er zijn een aantal aanpassingen nodig aan je `build.gradle` file om van een gewone Java applicatie over te schakelen naar een JavaFX-enabled applicatie. We hebben de **application** en **javafxplugin** plugins nodig onder `plugins {}`, verder ook een `javafx {}` property groep die bepaalt welke modules van JavaFX worden ingeladen:

```
plugins {
    id 'application'
    id 'org.openjfx.javafxplugin' version '0.0.13'
}

repositories {
    mavenCentral()
}

javafx {
    version = "17"
    modules = [ 'javafx.controls', 'javafx.fxml' ]
}

dependencies {
    implementation group: 'org.xerial', name: 'sqlite-jdbc', version: '3.43.2.0'
    implementation group: 'org.jdbi', name: 'jdbi3-core', version: '3.41.3'
    implementation group: 'org.jdbi', name: 'jdbi3-sqlite', version: '3.41.3'
    implementation group: 'org.jdbi', name: 'jdbi3-sqlobject', version: '3.41.3'

    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.9.0'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine'    
}

group 'be.kuleuven.javasql'
version '1.0-SNAPSHOT'
sourceCompatibility = 1.13
mainClassName = 'be.kuleuven.javasql.SqlFxMain'

jar {
    manifest {
        attributes 'Implementation-Title': project.getProperty('name'),
                'Implementation-Version': project.getProperty('version'),
                'Main-Class': project.getProperty('mainClassName')
    }
}
```

Herinner je het volgende over JavaFX:

- De main klasse leidt af van `Application` en laadt de hoofd-`.fxml` file in.
- Controllers hebben een `public void initialize()` methode waar action binding in wordt gedefinieerd. 
- `.fxml` files beheer je met SceneBuilder. Vergeet hier niet de link naar de fully qualified name van je controller klasse te plaatsen als `AnchorPane` attribuut: `fx:controller="be.kuleuven.javasql.controller.StudentController"`.

{{% notice warning %}}
Problemen met je JDK versie en Gradle versies? Raadpleeg de [Gradle Compatibiility Matrix](https://docs.gradle.org/current/userguide/compatibility.html). Gradle 6.7 of hoger ondersteunt JDK15. Gradle 7.3 of hoger ondersteunt JDK17. Let op met syntax wijzigingen bij Gradle 7+!<br/>
Je Gradle versie verhogen kan door de URL in `gradle/gradlew.properties` te wijzigen.<br/>
De laatste versie van JavaFX is 17---backwards compatible met JDK15 en hoger.
{{% /notice %}}

Voor onze studententabel visualisatie hebben we een `TableView` nodig. Daarnaast eventueel `Button`s om te editeren/toe te voegen/... Vergeet de `fx:id` van de tabel niet:

![](/img/javafx-id.jpg)

Kolommen (en de inhoud van de rijen) definiëren we in de controller zelf:

<div class="devselect">

```kt
@FXML
private lateinit var tblStudent: TableView<Student>

fun initialize() {
    tblStudent.getColumns().clear()
    val col: TableColumn<Student, String> = TableColumn<>("Naam").apply {
        setCellValueFactory(f -> ReadOnlyObjectWrapper<>(f.getValue().getMaam()))
    }
    with(tblStudent) {
        getColumns().add(col)
        getItems().add(Student("Joske", "Josmans", 124, true))
    }
}
```

```java
@FXML
private TableView<Student> tblStudent;

public void initialize() {
    tblStudent.getColumns().clear();
    TableColumn<Student, String> col = new TableColumn<>("Naam");
    col.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().getMaam()));
    tblStudent.getColumns().add(col);
    
    tblStudent.getItems().add(new Student("Joske", "Josmans", 124, true));
}
```
</div>

Merk op dat `TableView` een generisch type heeft, en we zo dus heel eenvoudig onze eigen POJO rechtstreeks kunnen mappen op de `Student` klasse! Als we dit opstarten krijgen we alvast één kolom te zien met de naam (`f` in de `CellValueFactory` is een wrapper waarvan de waarde de huidige student in de rij is. `getNaam()` zorgt ervoor dat de juiste waarde in de juiste cel komt te staan)

![](/img/fxmltable.jpg)

### 1.3.2 Oefeningen

**Quickstart project**: `examples/jdbc-fxml-start` in de [cursus repository](https://github.com/kuleuven-Diepenbeek/db-course) ([download repo zip](https://github.com/KULeuven-Diepenbeek/db-course/archive/refs/heads/main.zip)). Deze bevat reeds bovenstaande JDBC implementatie en een leeg gekoppeld JavaFx project. Om uit te voeren, klik op "Gradle" en voer target "run" uit (dus niet op "Play" in de main klasse!).

1. Werk bovenstaande voorbeeld verder uit voor alle kolommen. Voeg eerst testdata toe (`getItems().add(new student...`).
2. Probeer nu de controller te linken met de repository. De tabel items moeten overeenkomen met de repository items. Proficiat, je kijkt naar "live data"!
3. Voeg een knop **Voeg Toe** toe op het scherm, dat een ander FXML venster opent, waar je gegevens van de nieuwe student kan ingeven, en kan bewaren. De "bewaren" knop persisteert naar de database, sluit het venster, én refresht het studentenadmin overzichtsscherm. 

**Tip**: Vanuit een JavaFX controller een ander scherm openen is een kwestie van een nieuwe `Stage` en `Scene` object aan te maken:

<div class="devselect">

```kt
private fun showScherm() {
    val resourceName = "bla.fxml"
    val root = FXMLLoader.load(this::class.java..getResource(resourceName)) as AnchorPane;
    val stage = Stage().apply {
        setScene(Scene(root))
        setTitle("dinges")
        initModality(Modality.WINDOW_MODAL)
        show()
    }
}
```

```java
private void showScherm() {
    var resourceName = "bla.fxml";
    try {
        var stage = new Stage();
        var root = (AnchorPane) FXMLLoader.load(getClass().getClassLoader().getResource(resourceName));
        stage.setScene(new Scene(root));
        stage.setTitle("dinges");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.show();

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
```
</div>

Zit je vast? Raadpleeg de **TableView JavaDocs**: https://openjfx.io/javadoc/13/javafx.controls/javafx/scene/control/TableView.html

Bekijk een voorbeeld **Kotlin/JavaFX project** in de [github appdev-course repository](https://github.com/KULeuven-Diepenbeek/appdev-course/tree/main/examples/kotlin/walkerfx/src/main/kotlin/be/kuleuven/walkerfx). -->