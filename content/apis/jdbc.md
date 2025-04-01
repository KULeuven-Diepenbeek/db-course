---
title: JDBC
draft: false
weight: 2
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

**Gradle** dependency:
```groovy
// https://mvnrepository.com/artifact/mysql/mysql-connector-java
implementation 'mysql:mysql-connector-java:5.1.6'
```

Enkele belangrijke statements:

1. Een connectie naar een database vastleggen: 
```java
var connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/<database_name>", "root", "");
```
{{% notice warning %}}
Wil je meerdere queries tegelijk uitvoeren dan moet je dit ook nog specifiek vermelden in de driver door `?allowMultiQueries=true` toe te voegen aan de database url bv: `"jdbc:mysql://localhost:3306/<database_name>?allowMultiQueries=true"`
{{% /notice %}}
2. Een `SELECT` query uitvoeren: 
```java
var s = connection.createStatement(); var result = s.executeQuery("..."); var cell = result.getString("<column_name>");
```
3. Een `INSERT`/`UPDATE`/... query uitvoeren (die de structuur of inhoud van de database **wijzigt**): 
```java
var s = connection.createStatement(); s.executeUpdate("...");
```

### Een voorbeeld
Het volgende voorbeeld opent een verbinding naar een DB, maakt een tabel aan, voegt een record toe, en telt het aantal records:

```java
public static void main(String[] args) {
    try {
        App.createDb();
        App.verifyDbContents();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public static void createDb() throws SQLException {
    var connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/school?allowMultiQueries=true",
            "root", "");
    var s = connection.createStatement();
    s.executeUpdate("""
            DROP TABLE IF EXISTS student;

            CREATE TABLE student(
                studnr INT NOT NULL PRIMARY KEY,
                naam VARCHAR(200) NOT NULL,
                voornaam VARCHAR(200),
                goedbezig BOOLEAN
            );

            INSERT INTO student (studnr, naam, voornaam, goedbezig) VALUES
                (123, 'Trekhaak', 'Jaak', 0),
                (456, 'Peeters', 'Jos', 0),
                (890, 'Dongmans', 'Ding', 1);
            """);
    s.close();
    connection.close();
}

public static void verifyDbContents() throws SQLException {
    var connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/school?allowMultiQueries=true",
            "root", "");
    var s = connection.createStatement();
    var result = s.executeQuery("SELECT COUNT(*) as cnt FROM student;");
    while (result.next()) {
        System.out.println("Assert that number of rows is 3: " + (result.getInt("cnt") == 3));
        assert result.getInt("cnt") == 1;
    }
    s.close();
    connection.close();
}
```

Merk op dat `SQLException` een **checked exception** is die je constant moet meespelen in de method signature of expliciet moet opvangen. Het probleem van een `try { } catch { } finally { }` block is dat in de finally je ook geen `close()` kan uitvoeren zonder opnieuw een `try` block te openen... Inception!

Het `connection.close()` statement moet er voor zorgen dat voor elke request de connection netjes wordt afgesloten. Een database heeft meestal een **connection pool** van x aantal beschikbare connections, bijvoorbeeld 5. Als een connection per request niet wordt gesloten, heeft de volgende bezoeker van onze website geen enkele kans om zijn search query te lanceren, omdat de database dan zegt dat alle connecties zijn opgebruikt!

Merk op dat de String `jdbc:mysql://localhost:3306/school?allowMultiQueries=true` een **connectie met je MariaDB** aanmaakt en de meegegeven database, zodat je met PHPmyAdmin data kan inspecteren. Indien je een tabel aanmaakt de eerste keer, gaat dit de tweede keer crashen met _table already exists_. Houd hier dus rekening mee (e.v.t. met `IF NOT EXISTS`). 

{{% notice note %}}
Constant `CREATE TABLE()` statements issuen (bv. voor testing), vervuilen je broncode. Als je veel SQL moet uitvoeren is het beter om dit in een `.sql` bestand te bewaren in `src/main/resources` en eenmalig als een `String` in te lezen met:
```java
URI create_tables_path = Objects.requireNonNull(App.class.getClassLoader().getResource("create_tables.sql")).toURI();
String create_tables_sql = new String(Files.readAllBytes(Paths.get(create_tables_path)));
```
Die String kan je dan als Query executen in een statement!
{{% /notice %}}

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

### Demos

_We breiden [bovenstaand voorbeeld](#een-voorbeeld) uit:_

We maken hier weer gebruik van Gradle, maar aangezien onze database op onze Windows host draait gaan we geen verbinding kunnen maken via onze WSL. **_Daarvoor kan je je bestanden aanmaken in je WSL en dan de projectmap kopiëren naar je windows file explorer. Dan kan je de Gradle wrapper voor Windows gebruiken `./gradlew.bat`._**<br/>
Of je kan Gradle voor windows installeren. De stappen daarvoor vind je [hier](https://docs.gradle.org/current/userguide/installation.html#ex-installing-manually).

#### Alles in de main
Om dingen te doen met de database moeten we dus een aantal stappen doorlopen, die hier beschreven en gecodeerd zijn in een `main`-method:
```java
public static void main(String[] args){
    try{
        // CONNECT TO MYSQL
        var connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/school?allowMultiQueries=true", "root", "");

        // CREATE THE TABLES
        var statement = connection.createStatement();
        statement.executeUpdate("""
                DROP TABLE IF EXISTS student;

                CREATE TABLE student(
                    studnr INT NOT NULL PRIMARY KEY,
                    naam VARCHAR(200) NOT NULL,
                    voornaam VARCHAR(200),
                    goedbezig BOOLEAN
                );

                INSERT INTO student (studnr, naam, voornaam, goedbezig) VALUES
                    (123, 'Trekhaak', 'Jaak', 0),
                    (456, 'Peeters', 'Jos', 0),
                    (890, 'Dongmans', 'Ding', 1);
                """);
        statement.close();

        //VERIFY DATABASE CONTENT
        statement = connection.createStatement();
        var result = statement.executeQuery("SELECT COUNT(*) as cnt FROM student;");
        while (result.next()) {
            System.out.println("Assert that number of rows is 3: " + (result.getInt("cnt") == 3));
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
```java
public static void connectToDbMysql(String connectionString, String user, String pwd){...}
public static void createDbMysql(Connection connection, Statement statement){...}
public static void verifyDbContents(Connection connection, Statement statement){...}
public static ResultSet readFromDb(Connection connection, Statement statement, String query){...}
public static void updateDb(Connection connection, Statement statement, String updateStr){...}
public static void closeAllConnections(Connection connection, Statement statement){...}
```

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
{{% notice info %}}
<!-- EXSOL -->
**_[Hier](/files/jdbc-simple.zip) vind je een zipfolder met een oplossing voor onderstaande oefening: dashboard voor database met enkel studenten tabel_**
{{% /notice %}}

**Enkele vragen/oefeningen:**
1. Breid de `StudentRepository`-klasse uit met de volgende methoden (CREATE, READ, UPDATE, DELETE = **CRUD**):
```java
public class StudentRepository {
        public StudentRepository(Connection connection);
        public List<Student> getAllStudents();
        public void addStudentToDb(Student student);
        public Student getStudentsByStudnr(int studnr);
        public void updateStudentInDb(Student student);
        public void deleteStudentInDb(int studnr);
}
```
2. Hoe zou je bovenstaande `StudentRepository` unit (integratie) testen, zonder de "productie database" op te vullen met testdata? (Hint: kijk naar het constructor argument). Hoe kan je `getStudentsByName()` testen zonder de volgende oefening afgewerkt te hebben, die nieuwe studenten bewaren pas mogelijk maakt?
3. Breid dit uit met `addStudentToDb(Student)`.
4. Breid dit uit met `updateStudentInDb(Student)`. Wat moet je doen als deze student nog niet in de database zit? Welke gegevens update je wel en welke niet? 
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

#### EXTRA: In memory database

Je kan ook een **in-memory database** aanmaken, die volledig in RAM leeft en bij elke opstart opnieuw wordt aangemaakt, met de String `jdbc:sqlite:memory`. (Hiervoor gebruiken we dan de [sqlite JDBC-connector](https://github.com/xerial/sqlite-jdbc), hier gaan we in deze cursus echter niet verder op in.)


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

INSERT INTO vak(vaknr, vaknaam) VALUES (1, 'DAB');
INSERT INTO vak(vaknr, vaknaam) VALUES (2, 'SES');
INSERT INTO vak(vaknr, vaknaam) VALUES (3, 'FSWEB');

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

Breid de oefening van hierboven uit met alle data van de nieuwe database:
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
3. Voorzie nu voor de verschillende corresponderende `Repository`-klassen waar alle logica in te staan komt om de data over de verschillende klassen uit de database te halen en om te vormen tot Java objecten. (Je moet dus je `StudentRepository`-klasse aanpassen, een `VakRepository`-klasse en een `OpleidingRepository`-klasse aanmaken)
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